package uni.project.mylocalbooking.api;

 class Utility {

    public static String generateEncryptedPassword(String password){
        try {
            password = AESCrypt.encrypt(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }

    public static void callAPI(String jwt, String requestBody, String url, String method){
        if(jwt != null){
            APICall call = new APICall(jwt, method, requestBody, url);
            RequestQueueSingleton.getInstance().add(call.getRequest());
        }else{
            LoginAPI.addWaitingRequest(requestBody, url, method);
        }
    }
 }
