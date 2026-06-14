package me.tihon.model;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class HumanBeing implements Comparable<HumanBeing>, Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String owner;

    private String name;

    private Coordinates coordinates;

    private ZonedDateTime creationDate;

    private Boolean realHero;

    private Boolean hasToothpick;

    private Float impactSpeed;

    private String soundtrackName;

    private Long minutesOfWaiting;

    private WeaponType weaponType;

    private Car car;

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public void setWeaponType(WeaponType weaponType) {
        this.weaponType = weaponType;
    }

    public void setMinutesOfWaiting(Long minutesOfWaiting) {
        this.minutesOfWaiting = minutesOfWaiting;
    }

    public String getSoundtrackName() {
        return soundtrackName;
    }

    public void setSoundtrackName(String soundtrackName) {
        this.soundtrackName = soundtrackName;
    }

    public Float getImpactSpeed() {
        return impactSpeed;
    }

    public void setImpactSpeed(Float impactSpeed) {
        this.impactSpeed = impactSpeed;
    }

    public Boolean getHasToothpick() {
        return hasToothpick;
    }

    public void setHasToothpick(Boolean hasToothpick) {
        this.hasToothpick = hasToothpick;
    }

    public Boolean getRealHero() {
        return realHero;
    }

    public void setRealHero(Boolean realHero) {
        this.realHero = realHero;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HumanBeing(Integer id,
                      String owner,
                      String name,
                      Coordinates coordinates,
                      Boolean realHero,
                      Boolean hasToothpick,
                      Float impactSpeed,
                      String soundtrackName,
                      Long minutesOfWaiting,
                      WeaponType weaponType,
                      Car car) {

        this.id = id;
        this.owner = owner;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = ZonedDateTime.now();

        this.hasToothpick = hasToothpick;
        this.impactSpeed = impactSpeed;
        this.soundtrackName = soundtrackName;
        this.realHero = realHero;
        this.minutesOfWaiting = minutesOfWaiting;
        this.weaponType = weaponType;
        this.car = car;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getMinutesOfWaiting() {
        return minutesOfWaiting;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    @Override
    public int compareTo(HumanBeing o) {
        int cX = Float.compare(this.coordinates.getX(), o.coordinates.getX());
        if (cX != 0) return cX;
        return this.coordinates.getY().compareTo(o.coordinates.getY());
    }

    @Override
    public String toString() {
        return "HumanBeing{id=" + id +
                ", owner=" + owner +
                ", name=" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", realHero=" + realHero +
                ", hasToothpick=" + hasToothpick +
                ", impactSpeed=" + impactSpeed +
                ", soundtrackName=" + soundtrackName +
                ", minutesOfWaiting=" + minutesOfWaiting +
                ", weaponType=" + weaponType +
                ", car=" + car + "}";
    }

}


