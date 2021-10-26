package com.hanzec.P2PFileSyncServer.service;


import com.hanzec.P2PFileSyncServer.model.api.RegisterClientRequest;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.ClientAccount;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.UserAccount;
import com.hanzec.P2PFileSyncServer.model.data.manage.authenticate.Permission;
import com.hanzec.P2PFileSyncServer.model.data.manage.Group;
import com.hanzec.P2PFileSyncServer.model.exception.auth.ClientAlreadyExistException;
import com.hanzec.P2PFileSyncServer.model.exception.auth.EmailAlreadyExistException;
import com.hanzec.P2PFileSyncServer.model.exception.auth.PasswordNotMatchException;
import com.hanzec.P2PFileSyncServer.repository.manage.GroupRepository;
import com.hanzec.P2PFileSyncServer.repository.manage.account.ClientAccountepository;
import com.hanzec.P2PFileSyncServer.repository.manage.authenticate.PermissionRepository;
import com.hanzec.P2PFileSyncServer.repository.manage.account.UserAccountRepository;
import com.hanzec.P2PFileSyncServer.model.api.RegisterUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.util.Pair;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.security.Principal;
import java.util.HashMap;

@Service
public class AccountService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    private final GroupRepository groupRepository;

    private final  EntityManager clientEntityManager;

    private final PermissionRepository permissionRepository;

    private final UserAccountRepository userAccountRepository;

    private final ClientAccountepository clientAccountepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HashMap<String, Integer> register_code = new HashMap<>();

    public AccountService(PasswordEncoder passwordEncoder,
                          GroupRepository groupRepository,
                          PermissionRepository permissionRepository,
                          UserAccountRepository userAccountRepository,
                          ClientAccountepository clientAccountepository,
                          @Qualifier("entityManagerUser") EntityManager clientEntityManager) {
        //Update auto-injection Objects
        this.groupRepository = groupRepository;
        this.passwordEncoder = passwordEncoder;
        this.clientEntityManager = clientEntityManager;
        this.permissionRepository = permissionRepository;
        this.userAccountRepository = userAccountRepository;
        this.clientAccountepository = clientAccountepository;

        //initialized user role information
        initializeGroup();
        initializePermission();
        initializeUserAccount();
    }

    @Transactional
    public UserAccount createUser(RegisterUserRequest user) throws EmailAlreadyExistException {
        //There should not register with same email address
        if (userAccountRepository.existsByEmail(user.getEmail()))
            throw new EmailAlreadyExistException(user.getEmail());

        // user should belong to a group with same name
        Group newGroup;
        if(groupRepository.existsByName(user.getUsername())){
            newGroup = groupRepository.getGroupByName(user.getUsername());
        }else{
            groupRepository.save(
                    (newGroup =  new Group(user.getUsername(), user.getUsername() + "_default_group")));
            logger.debug("New Group :[" + user.getUsername() + ":" + newGroup.getId() + "] is created");
        }

        // create new user
        UserAccount newAccount = new UserAccount(
                user.getEmail(),
                user.getUsername(),
                passwordEncoder.encode(user.getPassword()), newGroup);

        // then create new account
        userAccountRepository.save(newAccount);

        logger.debug("New User with id :[" + user.getUsername() + "] is created");

        return newAccount;
    }

    @Transactional
    public Pair<ClientAccount,Integer> createNewClient(RegisterClientRequest registerClientRequest) throws ClientAlreadyExistException {
        // cannot create a new client with same ip address and machine id
        if(clientAccountepository.existsClientAccountByMachineIDOrIpAddress(
                registerClientRequest.getMachineID(),registerClientRequest.getIp()))
            throw new ClientAlreadyExistException(registerClientRequest.getMachineID(), registerClientRequest.getIp());

        ClientAccount newClientAccount = new ClientAccount(registerClientRequest.getMachineID(), registerClientRequest.getIp());

        //save to database
        clientAccountepository.save(newClientAccount);

        // add register code
        int reg_code = (int) ((Math.random() * (999999 - 100000)) + 10000);
        register_code.put(newClientAccount.getId(),reg_code);

        logger.info("New User with id :[" + newClientAccount.getMachineID() + "] is created");

        return Pair.of(newClientAccount, reg_code);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (userAccountRepository.existsByEmail(email))
            return userAccountRepository.getByEmail(email);
        else
            throw new UsernameNotFoundException("username " + email + " is not found");
    }

    @Transactional
    public void resetPassword(String newPassword,
                              String oldPassword,
                              Principal principal) throws PasswordNotMatchException {
        // first getting user object from database
        UserAccount userAccount = userAccountRepository.getById(principal.getName());

        // second changed the password
        if (!passwordEncoder.matches(userAccount.getPassword(), oldPassword))
            throw new PasswordNotMatchException(userAccount.getEmail());
        userAccount.setPassword(passwordEncoder.encode(newPassword));

        // commit the changes
        userAccountRepository.save(userAccount);
    }

    @Transactional
    public Integer enableClient(String clientID, String operatorUserID) {
        ClientAccount clientAccount = clientAccountepository.getById(clientID);

        clientAccount.enableClient(clientEntityManager.getReference(UserAccount.class,operatorUserID));

        clientAccountepository.save(clientAccount);

        return register_code.get(clientID);
    }


    public boolean checkPassword(String username, String password) {
        return passwordEncoder.matches(userAccountRepository.getById(username).getPassword(), password);
    }

    private void initializeGroup() {
        if (!groupRepository.existsByName("ROLE_ADMIN")) {
            Group newGroup = new Group("ROLE_ADMIN");
            groupRepository.save(newGroup);
        }
    }

    private void initializePermission() {

        if (!permissionRepository.existsByName("login")) {
            Permission permission = new Permission("modify_credential", "Permission for modify_credential");
            permissionRepository.save(permission);
        }

        if (!permissionRepository.existsByName("user_details")) {
            Permission permission = new Permission("user_details", "Permission for access user_details");
            permissionRepository.save(permission);
        }

        if (!permissionRepository.existsByName("modify_credential")) {
            Permission permission = new Permission("modify_credential", "Permission for modify_credential");
            permissionRepository.save(permission);
        }

        if (!permissionRepository.existsByName("enable_client")) {
            Permission permission = new Permission("enable_client", "Permission for enable_client");
            permissionRepository.save(permission);
        }
    }

    private void initializeUserAccount() {
        //Set up admin account if there is not
        if (groupRepository.existsByName("ROLE_ADMIN")) {
            UserAccount userAccount = new UserAccount(
                    "admin@example.com",
                    "admin",
                    passwordEncoder.encode("admin"),
                    groupRepository.getGroupByName("ROLE_ADMIN"),
                    permissionRepository.getPermissionByName("enable_client"));

            //save to database
            userAccountRepository.save(userAccount);
        }
    }
}
