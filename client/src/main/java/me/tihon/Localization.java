package me.tihon;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Localization {

    public enum Lang {
        RU("Русский", new Locale("ru", "RU")),
        DE("Deutsch", new Locale("de", "DE")),
        DA("Dansk", new Locale("da", "DA")),
        EN_IN("English (IN)", new Locale("en", "IN"));

        private final String displayName;
        private final Locale locale;

        Lang(String displayName, Locale locale) {
            this.displayName = displayName;
            this.locale = locale;
        }

        public Locale getLocale() { return locale; }

        @Override
        public String toString() { return displayName; }
    }

    private static final Localization INSTANCE = new Localization();
    public static Localization getInstance() { return INSTANCE; }
    private final ObjectProperty<Lang> langProperty = new SimpleObjectProperty<>(Lang.RU);
    private ResourceBundle currentBundle = new Messages_ru();
    private Localization() {}

    public ObjectProperty<Lang> langProperty() { return langProperty; }
    public Lang getCurrentLang() { return langProperty.get(); }

    public void setLang(Lang lang) {
        currentBundle = switch (lang) {
            case RU -> new Messages_ru();
            case DE -> new Messages_de();
            case DA -> new Messages_da();
            case EN_IN -> new Messages_en_IN();
        };
        langProperty.set(lang);
    }

    public String get(String key) {
        try {
            return currentBundle.getString(key);
        } catch (MissingResourceException e) {
            return "[" + key + "]";
        }
    }

    public static String t(String key) {
        return INSTANCE.get(key);
    }

    public String formatNumber(Number number) {
        return NumberFormat.getNumberInstance(getCurrentLang().getLocale()).format(number);
    }

    public String formatDec(Number number) {
        NumberFormat nf = NumberFormat.getNumberInstance(getCurrentLang().getLocale());
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return nf.format(number);
    }

    public String formatDateTime(ZonedDateTime zdt) {
        if (zdt == null) return "";
        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(getCurrentLang().getLocale());
        return zdt.format(dtf);
    }

    public String formatDate(ZonedDateTime zdt) {
        if (zdt == null) return "";
        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(getCurrentLang().getLocale());
        return zdt.format(dtf);
    }

    public static final String APP_TITLE          = "app.title";
    public static final String USER_LABEL         = "user.label";
    public static final String BTN_REFRESH        = "btn.refresh";
    public static final String BTN_INFO           = "btn.info";
    public static final String BTN_HELP           = "btn.help";
    public static final String BTN_ADD            = "btn.add";
    public static final String BTN_EDIT           = "btn.edit";
    public static final String BTN_DELETE         = "btn.delete";
    public static final String BTN_CLEAR          = "btn.clear";
    public static final String BTN_ADD_IF_MAX     = "btn.addIfMax";
    public static final String BTN_ADD_IF_MIN     = "btn.addIfMin";
    public static final String BTN_REMOVE_LOWER   = "btn.removeLower";
    public static final String BTN_PRINT_DESC     = "btn.printDesc";
    public static final String BTN_FILTER_NAME    = "btn.filterName";
    public static final String BTN_FILTER_MIN     = "btn.filterMinutes";
    public static final String BTN_SAVE           = "btn.save";
    public static final String BTN_CANCEL         = "btn.cancel";
    public static final String COL_ID             = "col.id";
    public static final String COL_OWNER          = "col.owner";
    public static final String COL_NAME           = "col.name";
    public static final String COL_X              = "col.x";
    public static final String COL_Y              = "col.y";
    public static final String COL_IMPACT         = "col.impact";
    public static final String COL_WEAPON         = "col.weapon";
    public static final String COL_REAL_HERO      = "col.realHero";
    public static final String COL_TOOTHPICK      = "col.toothpick";
    public static final String COL_SOUNDTRACK     = "col.soundtrack";
    public static final String COL_MINUTES        = "col.minutes";
    public static final String COL_CAR            = "col.car";
    public static final String COL_DATE           = "col.date";
    public static final String LOGIN_TITLE        = "login.title";
    public static final String LOGIN_USERNAME     = "login.username";
    public static final String LOGIN_PASSWORD     = "login.password";
    public static final String LOGIN_BTN_LOGIN    = "login.btn.login";
    public static final String LOGIN_BTN_REGISTER = "login.btn.register";
    public static final String LOGIN_ERR_EMPTY    = "login.error.empty";
    public static final String LOGIN_ERR_CONNECT  = "login.error.connect";
    public static final String EDIT_TITLE_ADD     = "edit.title.add";
    public static final String EDIT_TITLE_EDIT    = "edit.title.edit";
    public static final String EDIT_ERR_INVALID   = "edit.error.invalid";
    public static final String FILTER_LABEL       = "filter.label";
    public static final String FILTER_COL_LABEL   = "filter.col.label";
    public static final String SORT_LABEL         = "sort.label";
    public static final String SORT_ASC           = "sort.asc";
    public static final String SORT_DESC          = "sort.desc";
    public static final String MSG_SELECT         = "msg.select";
    public static final String MSG_OWN_ONLY_EDIT  = "msg.ownOnly.edit";
    public static final String MSG_OWN_ONLY_DEL   = "msg.ownOnly.delete";
    public static final String MSG_OPEN_EDITOR    = "msg.openEditor";
    public static final String FILTER_NAME_PROMPT = "filter.name.prompt";
    public static final String FILTER_MIN_PROMPT  = "filter.minutes.prompt";
    public static final String FILTER_MIN_ERR     = "filter.minutes.error";
    public static final String LANG_LABEL         = "lang.label";
    public static final String COLLECTION_EMPTY   = "collection.empty";
    public static final String INFO_POPUP_TITLE   = "info.popup.title";
    public static final String INFO_OPEN_EDITOR   = "info.openEditor";
    public static final String INFO_CLOSE         = "info.close";
    public static final String SECTION_OBJECT     = "section.object";
    public static final String SECTION_COLLECTION = "section.collection";
    public static final String SECTION_FILTERS    = "section.filters";

    public static class Messages_ru extends ListResourceBundle {
        @Override
        protected Object[][] getContents() {
            return new Object[][] {
                    { APP_TITLE,          "Менеджер HumanBeing" },
                    { USER_LABEL,         "Пользователь" },
                    { BTN_REFRESH,        "Обновить" },
                    { BTN_INFO,           "Информация" },
                    { BTN_HELP,           "Справка" },
                    { BTN_ADD,            "Добавить" },
                    { BTN_EDIT,           "Редактировать" },
                    { BTN_DELETE,         "Удалить" },
                    { BTN_CLEAR,          "Очистить" },
                    { BTN_ADD_IF_MAX,     "Добавить если макс." },
                    { BTN_ADD_IF_MIN,     "Добавить если мин." },
                    { BTN_REMOVE_LOWER,   "Удалить меньшие" },
                    { BTN_PRINT_DESC,     "По убыванию" },
                    { BTN_FILTER_NAME,    "По имени..." },
                    { BTN_FILTER_MIN,     "По минутам..." },
                    { BTN_SAVE,           "Сохранить" },
                    { BTN_CANCEL,         "Отмена" },
                    { COL_ID,             "ID" },
                    { COL_OWNER,          "Владелец" },
                    { COL_NAME,           "Имя" },
                    { COL_X,              "X" },
                    { COL_Y,              "Y" },
                    { COL_IMPACT,         "Скорость удара" },
                    { COL_WEAPON,         "Оружие" },
                    { COL_REAL_HERO,      "Герой" },
                    { COL_TOOTHPICK,      "Зубочистка" },
                    { COL_SOUNDTRACK,     "Саундтрек" },
                    { COL_MINUTES,        "Минуты" },
                    { COL_CAR,            "Машина" },
                    { COL_DATE,           "Дата создания" },
                    { LOGIN_TITLE,        "Вход / Регистрация" },
                    { LOGIN_USERNAME,     "Логин" },
                    { LOGIN_PASSWORD,     "Пароль" },
                    { LOGIN_BTN_LOGIN,    "Войти" },
                    { LOGIN_BTN_REGISTER, "Регистрация" },
                    { LOGIN_ERR_EMPTY,    "Введите логин и пароль" },
                    { LOGIN_ERR_CONNECT,  "Ошибка подключения" },
                    { EDIT_TITLE_ADD,     "Добавить объект" },
                    { EDIT_TITLE_EDIT,    "Редактировать объект" },
                    { EDIT_ERR_INVALID,   "Некорректные данные" },
                    { FILTER_LABEL,       "Фильтр:" },
                    { FILTER_COL_LABEL,   "по колонке:" },
                    { SORT_LABEL,         "Сортировать по:" },
                    { SORT_ASC,           "По возрастанию" },
                    { SORT_DESC,          "По убыванию" },
                    { MSG_SELECT,         "Выберите объект" },
                    { MSG_OWN_ONLY_EDIT,  "Можно редактировать только свои объекты" },
                    { MSG_OWN_ONLY_DEL,   "Можно удалять только свои объекты" },
                    { MSG_OPEN_EDITOR,    "Открыть редактор?" },
                    { FILTER_NAME_PROMPT, "Введите начало имени:" },
                    { FILTER_MIN_PROMPT,  "Показать объекты с минутами меньше:" },
                    { FILTER_MIN_ERR,     "Введите целое число" },
                    { LANG_LABEL,         "Язык" },
                    { COLLECTION_EMPTY,   "Коллекция пуста" },
                    { INFO_POPUP_TITLE,   "HumanBeing" },
                    { INFO_OPEN_EDITOR,   "Редактировать" },
                    { INFO_CLOSE,         "Закрыть" },
                    { SECTION_OBJECT,     "Объект:" },
                    { SECTION_COLLECTION, "Коллекция:" },
                    { SECTION_FILTERS,    "Фильтры:" },
            };
        }
    }

    public static class Messages_de extends ListResourceBundle {
        @Override
        protected Object[][] getContents() {
            return new Object[][] {
                    { APP_TITLE,          "HumanBeing-Verwaltung" },
                    { USER_LABEL,         "Benutzer" },
                    { BTN_REFRESH,        "Aktualisieren" },
                    { BTN_INFO,           "Information" },
                    { BTN_HELP,           "Hilfe" },
                    { BTN_ADD,            "Hinzufügen" },
                    { BTN_EDIT,           "Bearbeiten" },
                    { BTN_DELETE,         "Löschen" },
                    { BTN_CLEAR,          "Leeren" },
                    { BTN_ADD_IF_MAX,     "Hinzuf. wenn Max." },
                    { BTN_ADD_IF_MIN,     "Hinzuf. wenn Min." },
                    { BTN_REMOVE_LOWER,   "Kleinere entfernen" },
                    { BTN_PRINT_DESC,     "Absteigend anzeigen" },
                    { BTN_FILTER_NAME,    "Nach Name..." },
                    { BTN_FILTER_MIN,     "Nach Minuten..." },
                    { BTN_SAVE,           "Speichern" },
                    { BTN_CANCEL,         "Abbrechen" },
                    { COL_ID,             "ID" },
                    { COL_OWNER,          "Besitzer" },
                    { COL_NAME,           "Name" },
                    { COL_X,              "X" },
                    { COL_Y,              "Y" },
                    { COL_IMPACT,         "Aufprallgeschw." },
                    { COL_WEAPON,         "Waffe" },
                    { COL_REAL_HERO,      "Held" },
                    { COL_TOOTHPICK,      "Zahnstocher" },
                    { COL_SOUNDTRACK,     "Soundtrack" },
                    { COL_MINUTES,        "Minuten" },
                    { COL_CAR,            "Auto" },
                    { COL_DATE,           "Erstellungsdatum" },
                    { LOGIN_TITLE,        "Anmelden / Registrieren" },
                    { LOGIN_USERNAME,     "Benutzername" },
                    { LOGIN_PASSWORD,     "Passwort" },
                    { LOGIN_BTN_LOGIN,    "Anmelden" },
                    { LOGIN_BTN_REGISTER, "Registrieren" },
                    { LOGIN_ERR_EMPTY,    "Benutzername und Passwort eingeben" },
                    { LOGIN_ERR_CONNECT,  "Verbindungsfehler" },
                    { EDIT_TITLE_ADD,     "Objekt hinzufügen" },
                    { EDIT_TITLE_EDIT,    "Objekt bearbeiten" },
                    { EDIT_ERR_INVALID,   "Ungültige Eingabe" },
                    { FILTER_LABEL,       "Filter:" },
                    { FILTER_COL_LABEL,   "nach Spalte:" },
                    { SORT_LABEL,         "Sortieren nach:" },
                    { SORT_ASC,           "Aufsteigend" },
                    { SORT_DESC,          "Absteigend" },
                    { MSG_SELECT,         "Bitte Objekt auswählen" },
                    { MSG_OWN_ONLY_EDIT,  "Nur eigene Objekte bearbeiten" },
                    { MSG_OWN_ONLY_DEL,   "Nur eigene Objekte löschen" },
                    { MSG_OPEN_EDITOR,    "Editor öffnen?" },
                    { FILTER_NAME_PROMPT, "Namensanfang eingeben:" },
                    { FILTER_MIN_PROMPT,  "Objekte mit Minuten kleiner als:" },
                    { FILTER_MIN_ERR,     "Bitte eine ganze Zahl eingeben" },
                    { LANG_LABEL,         "Sprache" },
                    { COLLECTION_EMPTY,   "Sammlung ist leer" },
                    { INFO_POPUP_TITLE,   "HumanBeing" },
                    { INFO_OPEN_EDITOR,   "Bearbeiten" },
                    { INFO_CLOSE,         "Schließen" },
                    { SECTION_OBJECT,     "Objekt:" },
                    { SECTION_COLLECTION, "Sammlung:" },
                    { SECTION_FILTERS,    "Filter:" },
            };
        }
    }

    public static class Messages_da extends ListResourceBundle {
        @Override
        protected Object[][] getContents() {
            return new Object[][] {
                    { APP_TITLE,          "HumanBeing-Manager" },
                    { USER_LABEL,         "Bruger" },
                    { BTN_REFRESH,        "Opdater" },
                    { BTN_INFO,           "Information" },
                    { BTN_HELP,           "Hjælp" },
                    { BTN_ADD,            "Tilføj" },
                    { BTN_EDIT,           "Rediger" },
                    { BTN_DELETE,         "Slet" },
                    { BTN_CLEAR,          "Ryd" },
                    { BTN_ADD_IF_MAX,     "Tilføj hvis maks." },
                    { BTN_ADD_IF_MIN,     "Tilføj hvis min." },
                    { BTN_REMOVE_LOWER,   "Fjern mindre" },
                    { BTN_PRINT_DESC,     "Vis faldende" },
                    { BTN_FILTER_NAME,    "Efter navn..." },
                    { BTN_FILTER_MIN,     "Efter minutter..." },
                    { BTN_SAVE,           "Gem" },
                    { BTN_CANCEL,         "Annuller" },
                    { COL_ID,             "ID" },
                    { COL_OWNER,          "Ejer" },
                    { COL_NAME,           "Navn" },
                    { COL_X,              "X" },
                    { COL_Y,              "Y" },
                    { COL_IMPACT,         "Slagfart" },
                    { COL_WEAPON,         "Våben" },
                    { COL_REAL_HERO,      "Helt" },
                    { COL_TOOTHPICK,      "Tandstikker" },
                    { COL_SOUNDTRACK,     "Soundtrack" },
                    { COL_MINUTES,        "Minutter" },
                    { COL_CAR,            "Bil" },
                    { COL_DATE,           "Oprettelsesdato" },
                    { LOGIN_TITLE,        "Log ind / Registrer" },
                    { LOGIN_USERNAME,     "Brugernavn" },
                    { LOGIN_PASSWORD,     "Adgangskode" },
                    { LOGIN_BTN_LOGIN,    "Log ind" },
                    { LOGIN_BTN_REGISTER, "Registrer" },
                    { LOGIN_ERR_EMPTY,    "Indtast brugernavn og adgangskode" },
                    { LOGIN_ERR_CONNECT,  "Forbindelsesfejl" },
                    { EDIT_TITLE_ADD,     "Tilføj objekt" },
                    { EDIT_TITLE_EDIT,    "Rediger objekt" },
                    { EDIT_ERR_INVALID,   "Ugyldige data" },
                    { FILTER_LABEL,       "Filter:" },
                    { FILTER_COL_LABEL,   "efter kolonne:" },
                    { SORT_LABEL,         "Sorter efter:" },
                    { SORT_ASC,           "Stigende" },
                    { SORT_DESC,          "Faldende" },
                    { MSG_SELECT,         "Vælg et objekt" },
                    { MSG_OWN_ONLY_EDIT,  "Du kan kun redigere dine egne objekter" },
                    { MSG_OWN_ONLY_DEL,   "Du kan kun slette dine egne objekter" },
                    { MSG_OPEN_EDITOR,    "Åbn editor?" },
                    { FILTER_NAME_PROMPT, "Indtast navnepræfiks:" },
                    { FILTER_MIN_PROMPT,  "Vis objekter med minutter mindre end:" },
                    { FILTER_MIN_ERR,     "Indtast et heltal" },
                    { LANG_LABEL,         "Sprog" },
                    { COLLECTION_EMPTY,   "Samlingen er tom" },
                    { INFO_POPUP_TITLE,   "HumanBeing" },
                    { INFO_OPEN_EDITOR,   "Rediger" },
                    { INFO_CLOSE,         "Luk" },
                    { SECTION_OBJECT,     "Objekt:" },
                    { SECTION_COLLECTION, "Samling:" },
                    { SECTION_FILTERS,    "Filtre:" },
            };
        }
    }

    public static class Messages_en_IN extends ListResourceBundle {
        @Override
        protected Object[][] getContents() {
            return new Object[][] {
                    { APP_TITLE,          "HumanBeing Manager" },
                    { USER_LABEL,         "User" },
                    { BTN_REFRESH,        "Refresh" },
                    { BTN_INFO,           "Information" },
                    { BTN_HELP,           "Help" },
                    { BTN_ADD,            "Add" },
                    { BTN_EDIT,           "Edit" },
                    { BTN_DELETE,         "Delete" },
                    { BTN_CLEAR,          "Clear" },
                    { BTN_ADD_IF_MAX,     "Add If Max" },
                    { BTN_ADD_IF_MIN,     "Add If Min" },
                    { BTN_REMOVE_LOWER,   "Remove Lower" },
                    { BTN_PRINT_DESC,     "Print Descending" },
                    { BTN_FILTER_NAME,    "By Name..." },
                    { BTN_FILTER_MIN,     "By Minutes..." },
                    { BTN_SAVE,           "Save" },
                    { BTN_CANCEL,         "Cancel" },
                    { COL_ID,             "ID" },
                    { COL_OWNER,          "Owner" },
                    { COL_NAME,           "Name" },
                    { COL_X,              "X" },
                    { COL_Y,              "Y" },
                    { COL_IMPACT,         "Impact Speed" },
                    { COL_WEAPON,         "Weapon" },
                    { COL_REAL_HERO,      "Real Hero" },
                    { COL_TOOTHPICK,      "Has Toothpick" },
                    { COL_SOUNDTRACK,     "Soundtrack" },
                    { COL_MINUTES,        "Minutes" },
                    { COL_CAR,            "Car" },
                    { COL_DATE,           "Creation Date" },
                    { LOGIN_TITLE,        "Login / Register" },
                    { LOGIN_USERNAME,     "Username" },
                    { LOGIN_PASSWORD,     "Password" },
                    { LOGIN_BTN_LOGIN,    "Login" },
                    { LOGIN_BTN_REGISTER, "Register" },
                    { LOGIN_ERR_EMPTY,    "Please enter username and password" },
                    { LOGIN_ERR_CONNECT,  "Connection error" },
                    { EDIT_TITLE_ADD,     "Add Object" },
                    { EDIT_TITLE_EDIT,    "Edit Object" },
                    { EDIT_ERR_INVALID,   "Invalid input data" },
                    { FILTER_LABEL,       "Filter:" },
                    { FILTER_COL_LABEL,   "by column:" },
                    { SORT_LABEL,         "Sort by:" },
                    { SORT_ASC,           "Ascending" },
                    { SORT_DESC,          "Descending" },
                    { MSG_SELECT,         "Please select an object" },
                    { MSG_OWN_ONLY_EDIT,  "You may only edit your own objects" },
                    { MSG_OWN_ONLY_DEL,   "You may only delete your own objects" },
                    { MSG_OPEN_EDITOR,    "Open editor?" },
                    { FILTER_NAME_PROMPT, "Enter name prefix:" },
                    { FILTER_MIN_PROMPT,  "Show objects with minutes less than:" },
                    { FILTER_MIN_ERR,     "Please enter an integer" },
                    { LANG_LABEL,         "Language" },
                    { COLLECTION_EMPTY,   "Collection is empty" },
                    { INFO_POPUP_TITLE,   "HumanBeing" },
                    { INFO_OPEN_EDITOR,   "Edit" },
                    { INFO_CLOSE,         "Close" },
                    { SECTION_OBJECT,     "Object:" },
                    { SECTION_COLLECTION, "Collection:" },
                    { SECTION_FILTERS,    "Filters:" },
            };
        }
    }
}
