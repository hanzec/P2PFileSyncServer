package com.hanzec.P2PFileSyncServer.service;


import com.google.gson.Gson;
import com.hanzec.P2PFileSyncServer.model.api.RegisterClientRequest;
import com.hanzec.P2PFileSyncServer.model.api.RegisterUserRequest;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.ClientAccount;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.ClientGroup;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.UserAccount;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.UserGroup;
import com.hanzec.P2PFileSyncServer.model.data.manage.authenticate.Permission;
import com.hanzec.P2PFileSyncServer.model.exception.auth.ClientAlreadyExistException;
import com.hanzec.P2PFileSyncServer.model.exception.auth.EmailAlreadyExistException;
import com.hanzec.P2PFileSyncServer.model.exception.auth.PasswordNotMatchException;
import com.hanzec.P2PFileSyncServer.model.security.JwtPayload;
import com.hanzec.P2PFileSyncServer.repository.manage.account.ClientAccountepository;
import com.hanzec.P2PFileSyncServer.repository.manage.account.ClientGroupRepository;
import com.hanzec.P2PFileSyncServer.repository.manage.account.UserAccountRepository;
import com.hanzec.P2PFileSyncServer.repository.manage.account.UserGroupRepository;
import com.hanzec.P2PFileSyncServer.repository.manage.authenticate.PermissionRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class AccountService implements UserDetailsService {
    private final Gson gson;

    private final PasswordEncoder passwordEncoder;

    private final UserGroupRepository userGroupRepository;

    private final ClientGroupRepository clientGroupRepository;

    private final EntityManager clientEntityManager;

    private final PermissionRepository permissionRepository;

    private final UserAccountRepository userAccountRepository;

    private final ClientAccountepository clientAccountepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HashMap<String, Integer> register_code = new HashMap<>();

    public AccountService(Gson gson,
                          PasswordEncoder passwordEncoder,
                          UserGroupRepository userGroupRepository,
                          PermissionRepository permissionRepository,
                          UserAccountRepository userAccountRepository,
                          ClientAccountepository clientAccountepository,
                          ClientGroupRepository clientGroupRepository,
                          @Qualifier("entityManagerUser") EntityManager clientEntityManager) {

        //Update auto-injection Objects
        this.gson = gson;
        this.userGroupRepository = userGroupRepository;
        this.passwordEncoder = passwordEncoder;
        this.clientEntityManager = clientEntityManager;
        this.clientGroupRepository = clientGroupRepository;
        this.permissionRepository = permissionRepository;
        this.userAccountRepository = userAccountRepository;
        this.clientAccountepository = clientAccountepository;

        //initialized user role information
        initializeGroup();
        initializePermission();
        initializeUserAccount();
    }

    /**
     * Create a new user by RegisterUser Request
     *
     * @param user the request model for register user
     * @return the registered user entity
     * @throws EmailAlreadyExistException occurs when user's email is already registered
     */
    @Transactional
    public UserAccount createUser(RegisterUserRequest user) throws EmailAlreadyExistException {
        //There should not register with same email address
        if (userAccountRepository.existsByEmail(user.getEmail()))
            throw new EmailAlreadyExistException(user.getEmail());

        // user should belong to a group with same name
        UserGroup newUserGroup;
        if (userGroupRepository.existsByName(user.getUsername())) {
            newUserGroup = userGroupRepository.getGroupByName(user.getUsername());
        } else {
            userGroupRepository.save(
                    (newUserGroup = new UserGroup(user.getUsername(), user.getUsername() + "_default_group")));
            logger.debug("New Group :[" + user.getUsername() + ":" + newUserGroup.getId() + "] is created");
        }

        // create new user
        UserAccount newAccount = new UserAccount(
                user.getEmail(),
                user.getUsername(),
                passwordEncoder.encode(user.getPassword()), newUserGroup);

        // then create new account
        userAccountRepository.save(newAccount);

        logger.debug("New User with id :[" + user.getUsername() + "] is created");

        return newAccount;
    }


    /**
     * Register new client account by RegisterClientRequest model
     *
     * @param registerClientRequest the request model for register new client
     * @return reutrn a pair of data where first is the registered object and second is Integer with client register code
     * @throws ClientAlreadyExistException will occur when the combination of client id and ip address is already registered
     * @implNote for all non-exist groups, this function will add client to "DEFAULT_GROUP" created during this service's construction
     */
    @Transactional
    public Pair<ClientAccount, Integer> createNewClient(RegisterClientRequest registerClientRequest) throws ClientAlreadyExistException {
        // cannot create a new client with same ip address and machine id
        if (clientAccountepository.existsClientAccountByMachineIDOrIpAddress(
                registerClientRequest.getMachineID(), registerClientRequest.getIp()))
            throw new ClientAlreadyExistException(registerClientRequest.getMachineID(), registerClientRequest.getIp());

        ClientGroup group;
        if (clientGroupRepository.existsByName(registerClientRequest.getGroupName())) {
            group = clientGroupRepository.getGroupByName(registerClientRequest.getGroupName());
        } else {
            group = clientGroupRepository.getGroupByName("DEFAULT_GROUP");
            logger.warn("detect access of non-exist group[" + registerClientRequest.getGroupName() + "]");
        }

        ClientAccount newClientAccount = new ClientAccount(registerClientRequest.getMachineID(), registerClientRequest.getIp(), group);

        //save to database
        clientAccountepository.save(newClientAccount);

        // add register code
        int reg_code = (int) ((Math.random() * (999999 - 100000)) + 10000);
        register_code.put(newClientAccount.getId(), reg_code);

        logger.info("New User with id :[" + newClientAccount.getMachineID() + "] is created");

        return Pair.of(newClientAccount, reg_code);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (userAccountRepository.existsByEmail(email))
            return userAccountRepository.getByEmail(email);
        else
            throw new UsernameNotFoundException("username [" + email + "] is not found");
    }

    public boolean verifyClientToken(ClientAccount account, JWSObject jwsObject) throws JOSEException {
        JWSVerifier jwsVerifier = new MACVerifier(account.getJwtKey());

        if (!jwsObject.verify(jwsVerifier)) {
            return false;
        }

        var payload = gson.fromJson(jwsObject.getPayload().toString(), JwtPayload.class);

        return new Date().before(payload.getExp());
    }


    public String generateClientToken(String clientID) throws JOSEException {
        ClientAccount account = clientAccountepository.getById(clientID);
        return generateClientToken(account);
    }

    public String generateClientToken(ClientAccount clientAccount) throws JOSEException {
        var payload = new JwtPayload(clientAccount.getId(), Date.from(LocalDate.now().plusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT)
                .build();

        Payload jwtPayload = new Payload(gson.toJson(payload));
        JWSObject jwsObject = new JWSObject(jwsHeader, jwtPayload);
        JWSSigner jwsSigner = new MACSigner(clientAccount.getJwtKey());
        jwsObject.sign(jwsSigner);
        return jwsObject.serialize();
    }

    public ClientAccount loadClientByClientID(String clientID) throws UsernameNotFoundException {
        if (clientAccountepository.existsById(clientID))
            return clientAccountepository.getById(clientID);
        else
            throw new UsernameNotFoundException("client [" + clientID + "] is not found");
    }

    @Transactional
    public Map<String,String> getClientPeer(ClientAccount clientAccount){
        Map<String,String> ret = new HashMap<>();

        for (ClientAccount peer: clientAccount.getGroup().getClients()) {
            ret.put(peer.getId(),peer.getIpAddress());
        }

        return ret;
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

        clientAccount.enableClient(clientEntityManager.getReference(UserAccount.class, operatorUserID));

        clientAccountepository.save(clientAccount);

        register_code.remove(clientID);
    }


    public boolean checkPassword(String username, String password) {
        return passwordEncoder.matches(userAccountRepository.getById(username).getPassword(), password);
    }

    private void initializeGroup() {
        if (!userGroupRepository.existsByName("ROLE_ADMIN")) {
            UserGroup newUserGroup = new UserGroup("ROLE_ADMIN");
            userGroupRepository.save(newUserGroup);
        }

        if (!clientGroupRepository.existsByName("DEFAULT_GROUP")) {
            ClientGroup newClientGroup = new ClientGroup("DEFAULT_GROUP");
            clientGroupRepository.save(newClientGroup);
        }
    }

    private void initializePermission() {

        if (!permissionRepository.existsByName("modify_credential")) {
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
        if (userGroupRepository.existsByName("ROLE_ADMIN") && !userAccountRepository.existsByEmail("admin@example.com")) {
            UserAccount userAccount = new UserAccount(
                    "admin@example.com",
                    "admin",
                    passwordEncoder.encode("admin"),
                    userGroupRepository.getGroupByName("ROLE_ADMIN"),
                    permissionRepository.getPermissionByName("enable_client"));

            //save to database
            userAccountRepository.save(userAccount);
        }
    }
}
