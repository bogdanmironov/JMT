package bg.sofia.uni.fmi.mjt.w3.argumentpassing.exceptions.signature;

public class ExceptionSignature extends ExceptionSignatureBase {

    @Override
    public void fun(String s) throws MyException {
        try {
            super.fun(s);
        } catch (MyExceptionBase e) {
            throw new RuntimeException(e);
        }
    }

}

class MyException extends MyExceptionBase {

}