package com.hanzec.P2PFileSyncServer.service;


import com.hanzec.P2PFileSyncServer.model.api.RegisterClientRequest;
import com.hanzec.P2PFileSyncServer.model.data.manage.Client;
import com.hanzec.P2PFileSyncServer.model.data.manage.auth.Permission;
import com.hanzec.P2PFileSyncServer.model.data.manage.auth.Role;
import com.hanzec.P2PFileSyncServer.model.exception.auth.EmailAlreadyExistException;
import com.hanzec.P2PFileSyncServer.model.exception.auth.PasswordNotMatchException;
import com.hanzec.P2PFileSyncServer.model.data.manage.User;
import com.hanzec.P2PFileSyncServer.repository.manage.ClientRepository;
import com.hanzec.P2PFileSyncServer.repository.manage.auth.PermissionRepository;
import com.hanzec.P2PFileSyncServer.repository.manage.auth.RoleRepository;
import com.hanzec.P2PFileSyncServer.repository.manage.UserRepository;
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
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class AccountService implements UserDetailsService{
    /*
        Maybe a improve point
            - Session could reuse for whole class (2019-9-13)(FIXED)
                * Problem may happened when this instance is closed without close session
            - After some operation my gen waste @ redis see updateEmail
            - Cache may not accurate after delete user (2019-9-16)
            - exception
            - getUserEmail/getUserID need to improve （2019-10-26）(finished)
            - cache may not update when password is update (2019-10-27)
     */

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final ClientRepository clientRepository;

    private final PasswordEncoder passwordEncoder;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public AccountService(RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          ClientRepository clientRepository,
                          PermissionRepository permissionRepository,
                          UserRepository userRepository){

        //Update auto-injection Objects
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.clientRepository = clientRepository;

        if(!permissionRepository.existsById("user_details")){
            Permission permission = new Permission();
            permission.setPermissionName("user_details");
            permission.setDescription("Permission for access user_details");
            permissionRepository.save(permission);
        }

        if(!permissionRepository.existsById("modify_credential")){
            Permission permission = new Permission();
            permission.setPermissionName("modify_credential");
            permission.setDescription("Permission for modify_credential");
            permissionRepository.save(permission);
        }

        //initialized user role information
        if(!roleRepository.existsById("ROLE_USER")){
            Role role = new Role();
            role.setRoleName("ROLE_USER");
            role.setPermissions(permissionRepository.getAll());
            role.setDescription("Default Role for new User Role");
            roleRepository.save(role);
        }
        if(!roleRepository.existsById("ROLE_ADMIN")){
            Role role = new Role();
            role.setRoleName("ROLE_ADMIN");
            role.setPermissions(permissionRepository.getAll());
            role.setDescription("Default Role for new admin Role");
            roleRepository.save(role);
        }

        //Set up admin account if there is not
        if(roleRepository.existsById("ROLE_ADMIN")){
            User user = new User();

            //Update User Credential
            user.setEmail("admin@example.com");
            user.setJwtKey(UUID.randomUUID().toString());
            user.setRole(roleRepository.getById("ROLE_ADMIN"));
            user.setPassword(passwordEncoder.encode("admin"));

            //update User Details
            user.setUsername("admin");
            user.setLastName("admin");
            user.setFirstName("admin");
            //save to database
            userRepository.save(user);
        }
    }

    @Async
    @Transactional
    public void createUser(RegisterUserRequest user) throws EmailAlreadyExistException{
        //There should not register with same email address
        if(userRepository.existsById(user.getEmail()))
            throw new EmailAlreadyExistException(user.getEmail());

        User newUser = new User();
        //update User Details
        newUser.setUsername(user.getUsername());
        newUser.setLastName(user.getLastName());
        newUser.setFirstName(user.getFirstName());

        //Update User Credential
        newUser.setEmail(user.getEmail());
        newUser.setJwtKey(UUID.randomUUID().toString());
        newUser.setRole(roleRepository.getById("ROLE_USER"));
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        //save to database
        userRepository.save(newUser);

        logger.info("New User with id :[" + user.getUsername() + "] is created");
    }


    @Transactional
    public Client createNewClient(RegisterClientRequest user) {
        Client newClient = new Client();

        //update User Details
        newUser.setUsername(user.getUsername());
        newUser.setLastName(user.getLastName());
        newUser.setFirstName(user.getFirstName());

        //Update User Credential
        newUser.setEmail(user.getEmail());
        newUser.setJwtKey(UUID.randomUUID().toString());
        newUser.setRole(roleRepository.getById("ROLE_USER"));
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        //save to database
        clientRepository.save(newClient);

        logger.info("New User with id :[" + user.getUsername() + "] is created");
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (userRepository.existsById(email))
            return userRepository.getById(email);
        else
            throw new UsernameNotFoundException("username " + email + " is not found");
    }

    @Transactional
    public void resetPassword(String newPassword,
                              String oldPassword,
                              Principal principal) throws PasswordNotMatchException {
        // first getting user object from database
        User user = userRepository.getById(principal.getName());

        // second changed the password
        if(!passwordEncoder.matches(user.getPassword(),oldPassword))
            throw new PasswordNotMatchException(user.getEmail());
        user.setPassword(passwordEncoder.encode(newPassword));

        // third update password changede time
        user.setPasswordUpdateTime(ZonedDateTime.now());

        // commit the changes
        userRepository.save(user);
    }

    public boolean checkPassword(String username, String password){
        return passwordEncoder.matches(userRepository.getById(username).getPassword(), password);
    }
}
