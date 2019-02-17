package nb.dao;

import nb.domain.Customer;

import java.util.List;

public interface CustomerDao {

    Long create(Customer customer);
    Customer read(Long id);
    boolean update(Customer customer);
    boolean delete(Customer customer);

    Customer findByInn(long inn);

    List<Customer> findAll();
}
