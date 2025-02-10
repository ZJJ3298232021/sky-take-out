package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {

    /**
     *  查询地址簿
     * @param addressBook 查询条件
     * @return .
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 新增地址
     * @param addressBook .
     */
    void save(AddressBook addressBook);

    /**
     * 根据id查询
     * @param id .
     * @return .
     */
    AddressBook getById(Long id);

    /**
     * 修改地址
     * @param addressBook .
     */
    void update(AddressBook addressBook);

    /**
     * 设置默认地址
     * @param addressBook .
     */
    void setDefault(AddressBook addressBook);

    /**
     * 删除地址
     * @param id .
     */
    void deleteById(Long id);

}
