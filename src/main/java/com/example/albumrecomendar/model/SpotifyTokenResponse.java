package com.example.albumrecomendar.model;

public class SpotifyTokenResponse {

    private String access_token;
    private String token_type;
    private int expires_in;
    private String scope;

    public SpotifyTokenResponse(String a_t, String t_t, int e_i, String s){
        this.access_token = a_t;
        this.token_type = t_t;
        this.expires_in = e_i;
        this.scope = s;
    }

    public SpotifyTokenResponse(){
        this.access_token = "";
        this.token_type = "";
        this.expires_in = 0;
        this.scope = "";
    }

    public void setAccess_token(String a_t){
        this.access_token = a_t;
    }

    public String getAccess_token(){
        return this.access_token;
    }

    public void setToken_type(String t_t){
        this.token_type = t_t;
    }

    public String getToken_type(){
        return this.getToken_type();
    }

    public void setExpires_in(int e_i){
        this.expires_in = e_i;
    }

    public int getExpires_in(){
        return this.expires_in;
    }

    public void setScope(String s){
        this.scope = s;
    }

    public String getScope(){
        return this.scope;
    }
}
