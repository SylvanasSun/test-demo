package com.sun.sylvanas.application.echo_rpc;

/**
 * Created by SylvanasSun on 2017/5/16.
 */
public class EchoServiceImpl implements EchoService {
    @Override
    public String echo(String s) {
        return "Echo: " + s;
    }
}
