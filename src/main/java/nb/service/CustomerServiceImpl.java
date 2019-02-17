package nb.service;

import nb.dao.CustomerDao;
import nb.domain.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerDao customerDao;

    public CustomerServiceImpl() {
    }
    public CustomerServiceImpl(CustomerDao customerDao) {
        this.customerDao=customerDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomer(Long id) {
        return customerDao.read(id);
    }

    @Override
    public Long createCustomer(Customer customer) {
        return customerDao.create(customer);
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        return customerDao.update(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerByInn(Long inn) {
        return customerDao.findByInn(inn);
    }

    @Override
    @Transactional(readOnly = true)
    public List getAllCustomers() {
        return customerDao.findAll();
    }
}
