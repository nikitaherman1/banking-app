CREATE TABLE IF NOT EXISTS banking_user (
    id                  bigint          primary key ,
    name                varchar(500)    not null ,
    date_of_birth       date            not null ,
    password            varchar(500)    not null ,
    preferred_login     varchar(200)    not null unique
);

CREATE TABLE IF NOT EXISTS account (
    id                  bigint          primary key ,
    user_id             bigint          not null unique ,
    initial_balance     decimal         not null ,
    balance             decimal         not null check (balance >= 0),

    foreign key (user_id) references banking_user(id)
);
CREATE INDEX IF NOT EXISTS idx_account_user_id ON account(user_id);

CREATE TABLE IF NOT EXISTS email_data (
    id                  bigint          primary key ,
    user_id             bigint          not null ,
    email               varchar(200)    not null unique ,
    version             integer         default 0,

    foreign key (user_id) references banking_user(id)
);
CREATE INDEX IF NOT EXISTS idx_email_data_user_id ON email_data(user_id);

CREATE TABLE IF NOT EXISTS phone_data (
    id                  bigint          primary key ,
    user_id             bigint          not null ,
    phone               varchar(13)     not null unique,
    version             integer         default 0,

    foreign key (user_id) references banking_user(id)
);
CREATE INDEX IF NOT EXISTS idx_phone_data_user_id ON phone_data(user_id);

CREATE TABLE IF NOT EXISTS failed_accrual (
    id                  bigserial       primary key ,
    processed           boolean         not null default false ,
    retry_count         integer         not null default 0,
    error_message       varchar         not null ,
    created_at          timestamp       not null default now(),
    last_retry_at       timestamp
);

INSERT INTO banking_user (id, name, date_of_birth, password, preferred_login) VALUES
    (7327432779304620032, 'Иван Иванов', '1980-01-01', '$2a$10$2SdgXLfjm/q8qe.jTqvtD.2HCTT2lXmWvgleAjmbameoFVPJx3/fC', 'ivan_ivanov'),
    (7327432779304620033, 'Мария Петрова', '1992-05-15', '$2a$10$2SdgXLfjm/q8qe.jTqvtD.2HCTT2lXmWvgleAjmbameoFVPJx3/fC', 'maria_p'),
    (7327432779304620034, 'Алексей Смирнов', '1985-10-20', '$2a$10$2SdgXLfjm/q8qe.jTqvtD.2HCTT2lXmWvgleAjmbameoFVPJx3/fC', 'alex_sm'),
    (7327432779304620035, 'Ольга Кузнецова', '1995-03-10', '$2a$10$2SdgXLfjm/q8qe.jTqvtD.2HCTT2lXmWvgleAjmbameoFVPJx3/fC', 'olga_k'),
    (7327432779304620036, 'Дмитрий Соколов', '1988-08-25', '$2a$10$2SdgXLfjm/q8qe.jTqvtD.2HCTT2lXmWvgleAjmbameoFVPJx3/fC', 'dmitry_s');

INSERT INTO email_data (id, user_id, email) VALUES
    (7327432779304620041, 7327432779304620032, 'ivan@example.com'),
    (7327432779304620042, 7327432779304620033, 'maria@example.com'),
    (7327432779304620043, 7327432779304620034, 'alex@example.com'),
    (7327432779304620044, 7327432779304620035, 'olga@example.com'),
    (7327432779304620045, 7327432779304620036, 'dmitry@example.com');

INSERT INTO phone_data (id, user_id, phone) VALUES
    (7327432779304620051, 7327432779304620032, '+79123456789'),
    (7327432779304620052, 7327432779304620033, '+79234567890'),
    (7327432779304620053, 7327432779304620034, '+79345678901'),
    (7327432779304620054, 7327432779304620035, '+79456789012'),
    (7327432779304620055, 7327432779304620036, '+79567890123');

INSERT INTO account (id, user_id, initial_balance, balance) VALUES
    (7327432779304620061, 7327432779304620032, 1000.00, 1000.00),
    (7327432779304620062, 7327432779304620033, 2000.00, 2000.00),
    (7327432779304620063, 7327432779304620034, 3000.00, 3000.00),
    (7327432779304620064, 7327432779304620035, 4000.00, 4000.00),
    (7327432779304620065, 7327432779304620036, 5000.00, 5000.00);
