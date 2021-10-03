package com.hanzec.P2PFileSyncServer.service;


import com.hanzec.P2PFileSyncServer.model.api.RegisterClientRequest;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.ClientAccount;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.UserAccount;
import com.hanzec.P2PFileSyncServer.model.data.manage.authenticate.Permission;
import com.hanzec.P2PFileSyncServer.model.data.manage.Group;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.security.Principal;

@Service
public class AccountService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    private final GroupRepository groupRepository;

    private final  EntityManager clientEntityManager;

    private final PermissionRepository permissionRepository;

    private final UserAccountRepository userAccountRepository;

    private final ClientAccountepository clientAccountepository;



    private final Logger logger = LoggerFactory.getLogger(this.getClass());


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
    public ClientAccount createNewClient(String machineID, String ipAddress) {
        ClientAccount newClientAccount = new ClientAccount(machineID, ipAddress);

        //save to database
        clientAccountepository.save(newClientAccount);

        logger.info("New User with id :[" + newClientAccount.getMachineID() + "] is created");

        return newClientAccount;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (userAccountRepository.existsById(email))
            return userAccountRepository.getById(email);
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
    public void enableClient(String clientID, String operatorUserID) {
        ClientAccount clientAccount = clientAccountepository.getById(clientID);

        clientAccount.enableClient(clientEntityManager.getReference(UserAccount.class,operatorUserID));

        clientAccountepository.save(clientAccount);
    }


    public boolean checkPassword(String username, String password) {
        return passwordEncoder.matches(userAccountRepository.getById(username).getPassword(), password);
    }

    private void initializeGroup() {

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
    }

    private void initializeUserAccount() {
        //Set up admin account if there is not
        if (groupRepository.existsByName("ROLE_ADMIN")) {
            UserAccount userAccount = new UserAccount(
                    "admin@example.com",
                    "admin",
                    passwordEncoder.encode("admin"),
                    groupRepository.getGroupByName("ROLE_ADMIN"));

            //save to database
            userAccountRepository.save(userAccount);
        }
    }
}
