package me.tihon.manager;

import me.tihon.model.HumanBeing;

import java.time.ZonedDateTime;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

public class CollectionManager {

    private TreeSet<HumanBeing> collection = new TreeSet<>();
    private final ZonedDateTime initDate = ZonedDateTime.now();
    private final ReentrantLock lock = new ReentrantLock();

    public void add(HumanBeing h) {
        lock.lock();
        try {
            collection.add(h);
        } finally {
            lock.unlock();
        }
    }

    public TreeSet<HumanBeing> getCollection() {
        lock.lock();
        try {
            return new TreeSet<>(collection);
        } finally {
            lock.unlock();
        }
    }

    public void setCollection(TreeSet<HumanBeing> collection) {
        lock.lock();
        try {
            this.collection = new TreeSet<>(collection);
        } finally {
            lock.unlock();
        }
    }

    public ZonedDateTime getInitDate() {
        return initDate;
    }

    public HumanBeing getById(int id) {
        lock.lock();
        try {
            return collection.stream()
                    .filter(h -> h.getId() == id)
                    .findFirst()
                    .orElse(null);
        } finally {
            lock.unlock();
        }
    }

    public boolean removeById(int id) {
        lock.lock();
        try {
            return collection.removeIf(h -> h.getId() == id);
        } finally {
            lock.unlock();
        }
    }
}

