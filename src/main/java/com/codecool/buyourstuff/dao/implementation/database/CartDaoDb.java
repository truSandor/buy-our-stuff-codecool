package com.codecool.buyourstuff.dao.implementation.database;

import com.codecool.buyourstuff.dao.CartDao;
import com.codecool.buyourstuff.model.Cart;

import java.sql.Connection;
import java.util.List;

public class CartDaoDb implements CartDao {
    private final Connection connection;
    public CartDaoDb(Connection connection){
        this.connection = connection;
    }
    @Override
    public void add(Cart cart) {

    }

    @Override
    public Cart find(int id) {
        return null;
    }

    @Override
    public void remove(int id) {

    }

    @Override
    public void clear() {

    }

    @Override
    public List<Cart> getAll() {
        return null;
    }
}
