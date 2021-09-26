package com.hanzec.P2PFileSyncServer.service;


import com.hanzec.P2PFileSyncServer.model.api.RegisterClientRequest;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.ClientAccount;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.UserAccount;
import com.hanzec.P2PFileSyncServer.model.data.manage.authenticate.Permission;
import com.hanzec.P2PFileSyncServer.model.data.manage.authenticate.UserRole;
import com.hanzec.P2PFileSyncServer.model.exception.auth.EmailAlreadyExistException;
import com.hanzec.P2PFileSyncServer.model.exception.auth.PasswordNotMatchException;
import com.hanzec.P2PFileSyncServer.repository.manage.account.ClientAccountepository;
import com.hanzec.P2PFileSyncServer.repository.manage.authenticate.PermissionRepository;
import com.hanzec.P2PFileSyncServer.repository.manage.authenticate.UserRoleRepository;
import com.hanzec.P2PFileSyncServer.repository.manage.account.UserAccountRepository;
import com.hanzec.P2PFileSyncServer.model.api.RegisterUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
public class AccountService implements UserDetailsService{

    private final UserRoleRepository roleRepository;

    private final UserAccountRepository userAccountRepository;

    private final ClientAccountepository clientAccountepository;

    private final PasswordEncoder passwordEncoder;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public AccountService(UserRoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          ClientAccountepository clientAccountepository,
                          PermissionRepository permissionRepository,
                          UserAccountRepository userAccountRepository){

        //Update auto-injection Objects
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userAccountRepository = userAccountRepository;
        this.clientAccountepository = clientAccountepository;

        if(!permissionRepository.existsByName("user_details")){
            Permission permission = new Permission("user_details", "Permission for access user_details");
            permissionRepository.save(permission);
        }

        if(!permissionRepository.existsByName("modify_credential")){
            Permission permission = new Permission("modify_credential", "Permission for modify_credential");
            permissionRepository.save(permission);
        }

        //initialized user role information
        if(!roleRepository.existsByName("ROLE_USER")){
            UserRole userRole = new UserRole("ROLE_USER","Default Role for new User Role");
            userRole.getPermissions().addAll(permissionRepository.getAll());
            roleRepository.save(userRole);
        }
        if(!roleRepository.existsByName("ROLE_ADMIN")){
            UserRole userRole = new UserRole("ROLE_ADMIN","Default Role for new admin Role" );
            userRole.getPermissions().addAll(permissionRepository.getAll());
            roleRepository.save(userRole);
        }

        //Set up admin account if there is not
        if(roleRepository.existsByName("ROLE_ADMIN")){
            UserAccount userAccount = new UserAccount();

            //Update User Credential
            userAccount.setEmail("admin@example.com");
            userAccount.setRole(roleRepository.getUserRoleByName("ROLE_ADMIN"));
            userAccount.setPassword(passwordEncoder.encode("admin"));

            //update User Details
            userAccount.setUsername("admin");

            //save to database
            userAccountRepository.save(userAccount);
        }
    }

    @Async
    @Transactional
    public void createUser(RegisterUserRequest user) throws EmailAlreadyExistException{
        //There should not register with same email address
        if(userAccountRepository.existsById(user.getEmail()))
            throw new EmailAlreadyExistException(user.getEmail());

        UserAccount newUserAccount = new UserAccount();
        //update User Details
        newUserAccount.setUsername(user.getUsername());

        //Update User Credential
        newUserAccount.setEmail(user.getEmail());
        newUserAccount.setRole(roleRepository.getById(1));
        newUserAccount.setPassword(passwordEncoder.encode(user.getPassword()));

        //save to database
        userAccountRepository.save(newUserAccount);

        logger.info("New User with id :[" + user.getUsername() + "] is created");
    }

//    @Async
//    @Transactional
//    public void createNewClient(RegisterClientRequest user) {
//        ClientAccount newClientAccount = new ClientAccount();
//
//        //save to database
//        clientAccountepository.save(newClientAccount);
//
//        logger.info("New User with id :[" + user.getUsername() + "] is created");
//    }

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
        if(!passwordEncoder.matches(userAccount.getPassword(),oldPassword))
            throw new PasswordNotMatchException(userAccount.getEmail());
        userAccount.setPassword(passwordEncoder.encode(newPassword));

        // commit the changes
        userAccountRepository.save(userAccount);
    }

    public boolean checkPassword(String username, String password){
        return passwordEncoder.matches(userAccountRepository.getById(username).getPassword(), password);
    }
}
