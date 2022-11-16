package uni.project.mylocalbooking.api;

import uni.project.mylocalbooking.models.AppUser;
import uni.project.mylocalbooking.models.Client;
import uni.project.mylocalbooking.models.Provider;

class JSONBodyGenerator {

    public static String generateRegisterBody(AppUser user, String password){
        String jsonBody = "{" +
                "\"cellphone\": \"" + user.cellphone + "\", " +
                "\"firstname\": \"" + user.firstname + "\", " +
                "\"email\": \"" + user.email + "\", " +
                "\"lastname\": \"" + user.lastname + "\", " +
                "\"dob\": \"" + "2001-04-20" + "\", " +
                "\"password_digest\": \"" + password + "\", ";

        if(user instanceof Client){
            Client client = (Client) user;
            jsonBody += "\"Client\": {" +
                    "\"lat\": " + client.position.latitude + ", " +
                    "\"lng\": " + client.position.longitude +
                    "}}";
        } else {
            Provider provider = (Provider) user;
            jsonBody += "\"Provider\": {" +
                    "\"isverified\": " + (provider.verified ? 1 : 0) + ", " +
                    "\"maxstrikes\": " + provider.maxStrikes + ", " +
                    "\"companyname\": \"" + provider.companyName +
                    "\"}}";
        }

        return jsonBody;
    }

    public static String generateNewPasswordBody(String new_password){
        String jsonBody = "{\"new_password\": \"" + new_password + "\"}";
        return jsonBody;
    }
}
