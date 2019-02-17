package nb.service;

import nb.domain.Customer;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CustomerService {

    Customer getCustomer(Long id);

    Long createCustomer(Customer customer);

    boolean updateCustomer(Customer customer);

    @Transactional(readOnly = true)
    Customer getCustomerByInn(Long customerNum);

    List getAllCustomers();
}
