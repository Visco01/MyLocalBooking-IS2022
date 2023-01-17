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

    public static <T> void callAPI(String jwt, String requestBody, String url, String method, RunOnResponse<T> runOnResponse, boolean isArray){
        new CallbackAPICall<T>(jwt, method, requestBody, url, runOnResponse, isArray).call();
    }

    public static <T> BlockingAPICall<T> callAPI(String jwt, String requestBody, String url, String method, boolean isArray){
        BlockingAPICall<T> call = new BlockingAPICall<T>(jwt, method, requestBody, url, isArray);
        call.call();
        return call;
    }
}
