package ru.neonzoff.guidevologdamvc.domain;

public enum ACTIONS {
    CHANGE_PROFILE("изменил(а) свой профиль"),
    CHANGE_USER_PROFILE("изменил(а) профиль пользователя с id=%d и username=%s"),
    REGISTER("пользователь зарегистрировался на сайте, его id=%d"),
    INIT_ROLES("инициализированы следующие роли %s"),
    INIT_ADMIN_ACCOUNT("инициализирован аккаунт администратора с username=%s"),
    INIT_TYPE_ENTITY("инициализированы типы сущностей с id=%s"),
    INIT_CONTACT_TYPE("инициализированы типы контактов с id=%s"),
    INIT_TAG("инициализированы теги с id=%s"),
    UPLOAD_FILE("загрузил(а) файл с названием %s"),
    DELETE_FILE("удалил(а) файл с названием %s"),
    UPLOAD_IMAGE("загрузил(а) изображение с id=%s"),
    DELETE_IMAGE("удалил(а) изображение с id=%s"),
    CREATE_TYPE_ENTITY("создал(а) тип сущности с id=%d"),
    UPDATE_TYPE_ENTITY("изменил(а) тип сущности с id=%d"),
    DELETE_TYPE_ENTITY("удалил(а) тип сущности с id=%d"),
    CREATE_HOME_ENTITY("создал(а) сущность на главном экране"),
    UPDATE_HOME_ENTITY("изменил(а) сущность на главном экране"),
    CREATE_ENTITY("создал(а) сущность с id=%d"),
    UPDATE_ENTITY("изменил(а) сущность с id=%d"),
    DELETE_ENTITY("удалил(а) сущность с id=%d"),
    CREATE_TRACK("создал(а) трек с id=%d"),
    UPDATE_TRACK("изменил(а) трек с id=%d"),
    DELETE_TRACK("удалил(а) трек с id=%d"),
    CREATE_TAG("создал(а) тег с id=%d"),
    UPDATE_TAG("изменил(а) тег с id=%d"),
    DELETE_TAG("удалил(а) тег с id=%d"),
    CREATE_STREET("создал(а) улицу с id=%d"),
    UPDATE_STREET("изменил(а) улицу с id=%d"),
    DELETE_STREET("удалил(а) улицу с id=%d"),
    CREATE_CONTACT_TYPE("создал(а) тип контактов с id=%d"),
    UPDATE_CONTACT_TYPE("изменил(а) тип контактов с id=%d"),
    DELETE_CONTACT_TYPE("удалил(а) тип контактов с id=%d"),
    CREATE_CONTACT("создал(а) контакт с id=%d"),
    UPDATE_CONTACT("изменил(а) контакт с id=%d"),
    DELETE_CONTACT("удалил(а) контакт с id=%d");

    private String action;

    ACTIONS(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}
