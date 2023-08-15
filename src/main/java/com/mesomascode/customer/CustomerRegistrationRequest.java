package com.mesomascode.customer;

public record CustomerRegistrationRequest (
        String name,
        String email,
        Integer age

){
}
