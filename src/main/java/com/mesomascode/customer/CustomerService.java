package com.mesomascode.customer;

import com.mesomascode.exception.DuplicateResourceException;
import com.mesomascode.exception.RequestValidationException;
import com.mesomascode.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jpa") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers(){
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomerById(Integer id){
        return customerDao.selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "customer with id [%s] not found".formatted(id)
                ));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest){
        //check if email exists
        String email = customerRegistrationRequest.email();
        if (customerDao.existsPersonWithEmail(email)){
            throw new DuplicateResourceException("email already taken");
        }
        Customer customer = new Customer(customerRegistrationRequest.name(),
                customerRegistrationRequest.email(), customerRegistrationRequest.age());
        //add
        customerDao.insertCustomer(customer);
    }

    public void deleteCustomerById(Integer id){
        //check if id exists
        if (!customerDao.existsPersonWithId(id)){
            throw new ResourceNotFoundException(
                    "customer with id [%s] not found".formatted(id)
            );
        }
        //delete
        customerDao.deleteCustomerById(id);
    }

    public void updateCustomerById(Integer id,CustomerUpdateRequest customerUpdateRequest){
        Customer existingCustomer = getCustomerById(id);
        boolean changes = false;
        String name = customerUpdateRequest.name();
        String email = customerUpdateRequest.email();
        Integer age = customerUpdateRequest.age();
        if(name != null && !name.equals(existingCustomer.getName())){
            existingCustomer.setName(name);
            changes = true;
        }
        if(age != null && !age.equals(existingCustomer.getAge())){
            existingCustomer.setAge(age);
            changes = true;
        }
        if(email != null && !email.equals(existingCustomer.getEmail())){
            if (customerDao.existsPersonWithEmail(email)){
                throw new DuplicateResourceException("email already taken");
            }
            existingCustomer.setEmail(email);
            changes = true;
        }
        if(!changes){
            throw new RequestValidationException("no data changes found");
        }
        customerDao.UpdateCustomer(existingCustomer);
    }


}
