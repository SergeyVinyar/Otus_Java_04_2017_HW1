package ru.vinyarsky.hw9;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User extends DataSet {

    @Column
    public String name;

    @Column(nullable = false, precision = 3)
    public int age = 0;
}
