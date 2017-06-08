package ru.vinyarsky.hw9;

import javax.persistence.Id;

/* package */ abstract class DataSet {

    @Id
    long id;

    public long getId() {
        return id;
    }
}
