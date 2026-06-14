package me.tihon.database;

import me.tihon.model.*;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TreeSet;

public class HumanBeingDAO {

    private final DatabaseManager databaseManager;
    public HumanBeingDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public TreeSet<HumanBeing> loadAll() {
        TreeSet<HumanBeing> collection = new TreeSet<>();
        String sql = "SELECT * FROM human_beings ORDER BY id";
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {

                Integer id = rs.getInt("id");
                String owner = rs.getString("owner_name");
                String name = rs.getString("name");
                float x = rs.getFloat("x");
                Integer y = rs.getInt("y");
                Coordinates coordinates = new Coordinates(x, y);
                ZonedDateTime creationDate = rs.getTimestamp("creation_date")
                        .toInstant().atZone(ZoneId.systemDefault());
                Boolean realHero = rs.getBoolean("real_hero");
                boolean ht = rs.getBoolean("has_toothpick");
                Boolean hasToothpick = rs.wasNull() ? null : ht;
                Float impactSpeed = rs.getFloat("impact_speed");
                String soundtrackName = rs.getString("soundtrack_name");
                long mw = rs.getLong("minutes_of_waiting");
                Long minutesOfWaiting = rs.wasNull() ? null : mw;
                WeaponType weaponType = WeaponType.valueOf(rs.getString("weapon_type"));
                boolean carCoolValue = rs.getBoolean("car_cool");
                Car car = rs.wasNull() ? null : new Car(carCoolValue);

                HumanBeing hum = new HumanBeing(id,
                        owner,
                        name,
                        coordinates,
                        realHero,
                        hasToothpick,
                        impactSpeed,
                        soundtrackName,
                        minutesOfWaiting,
                        weaponType,
                        car);
                hum.setCreationDate(creationDate);
                collection.add(hum);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return collection;
    }

    public boolean insert(HumanBeing h) {
        String sql = "INSERT INTO human_beings (" +
                "owner_name, " +
                "name, " +
                "x, " +
                "y, " +
                "creation_date, " +
                "real_hero, " +
                "has_toothpick, " +
                "impact_speed, " +
                "soundtrack_name, " +
                "minutes_of_waiting, " +
                "weapon_type, " +
                "car_cool" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = databaseManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, h.getOwner());
            ps.setString(2, h.getName());
            ps.setFloat(3, h.getCoordinates().getX());
            ps.setInt(4, h.getCoordinates().getY());
            ps.setTimestamp(5, Timestamp.from(h.getCreationDate().toInstant()));
            ps.setBoolean(6, h.getRealHero());

            if (h.getHasToothpick() == null) {
                ps.setNull(7, java.sql.Types.BOOLEAN);
            } else {
                ps.setBoolean(7, h.getHasToothpick());
            }

            ps.setFloat(8, h.getImpactSpeed());
            ps.setString(9, h.getSoundtrackName());

            if (h.getMinutesOfWaiting() == null) {
                ps.setNull(10, java.sql.Types.BIGINT);
            } else {
                ps.setLong(10, h.getMinutesOfWaiting());
            }

            ps.setString(11, h.getWeaponType().name());

            if (h.getCar() == null) {
                ps.setNull(12, java.sql.Types.BOOLEAN);
            } else {
                ps.setBoolean(12, h.getCar().isCool());
            }

            int rows = ps.executeUpdate();
            if (rows == 0) return false;

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                h.setId(keys.getInt(1));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clearByOwner(String owner) {
        String sql = "DELETE FROM human_beings WHERE owner_name = ?";
        try (Connection c = databaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, owner);
            int rows = ps.executeUpdate();

            System.out.println("DELETE OWNER = [" + owner + "]");
            System.out.println("ROWS = " + rows);
            return ps.executeUpdate() >= 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(HumanBeing h) {

        String sql = """
        UPDATE human_beings
        SET name = ?,
            x = ?,
            y = ?,
            real_hero = ?,
            has_toothpick = ?,
            impact_speed = ?,
            soundtrack_name = ?,
            minutes_of_waiting = ?,
            weapon_type = ?,
            car_cool = ?
        WHERE id = ?
        """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, h.getName());
            ps.setFloat(2, h.getCoordinates().getX());
            ps.setInt(3, h.getCoordinates().getY());
            ps.setBoolean(4, h.getRealHero());
            if (h.getHasToothpick() == null) {
                ps.setNull(5, Types.BOOLEAN);
            } else {
                ps.setBoolean(5, h.getHasToothpick());
            }
            ps.setFloat(6, h.getImpactSpeed());
            ps.setString(7, h.getSoundtrackName());
            if (h.getMinutesOfWaiting() == null) {
                ps.setNull(8, Types.BIGINT);
            } else {
                ps.setLong(8, h.getMinutesOfWaiting());
            }
            ps.setString(9, h.getWeaponType().name());
            if (h.getCar() == null) {
                ps.setNull(10, Types.BOOLEAN);
            } else {
                ps.setBoolean(10, h.getCar().isCool());
            }
            ps.setInt(11, h.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM human_beings WHERE id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

