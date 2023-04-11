package petpalooza.Services;

import petpalooza.Entities.User;

import java.util.List;

public interface IUser {
    public List<User> retrieveallUsers();

    public User updateUser(User user);
    public User addeUser(User user);
    public void deletUser(Long id);
    public User getById(long id);

    List<User> getAllUser();

    User addNewUser(User user);

    User updateUser(User user);

    void deleteUser( Long idUser);

    User findUserByID(Long idUser);

    Boolean usernameAlreadyExist(String username);
    Boolean emailExist(String email);

}
