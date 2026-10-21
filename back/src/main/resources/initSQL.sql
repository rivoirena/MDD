-- =========================================================
-- Initialisation de la base de données
-- =========================================================
CREATE DATABASE IF NOT EXISTS mdd
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE mdd;

-- ---------------------------------------------------------
-- Table users
-- ---------------------------------------------------------
CREATE TABLE users (
                       id          BIGINT        NOT NULL AUTO_INCREMENT,
                       email       VARCHAR(255)  NOT NULL,
                       username    VARCHAR(50)   NOT NULL,
                       password    VARCHAR(255)  NOT NULL,
                       created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
                           ON UPDATE CURRENT_TIMESTAMP,
                       PRIMARY KEY (id),
                       UNIQUE KEY uk_users_email (email),
                       UNIQUE KEY uk_users_username (username)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- Table themes
-- ---------------------------------------------------------
CREATE TABLE themes (
                        id           BIGINT        NOT NULL AUTO_INCREMENT,
                        title        VARCHAR(100)  NOT NULL,
                        description  TEXT          NOT NULL,
                        PRIMARY KEY (id),
                        UNIQUE KEY uk_themes_title (title)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- Table articles
-- ---------------------------------------------------------
CREATE TABLE articles (
                          id          BIGINT        NOT NULL AUTO_INCREMENT,
                          title       VARCHAR(255)  NOT NULL,
                          content     TEXT          NOT NULL,
                          created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          author_id   BIGINT        NOT NULL,
                          theme_id    BIGINT        NOT NULL,
                          PRIMARY KEY (id),
                          CONSTRAINT fk_articles_author
                              FOREIGN KEY (author_id) REFERENCES users (id),
                          CONSTRAINT fk_articles_theme
                              FOREIGN KEY (theme_id) REFERENCES themes (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- Table comments
-- ---------------------------------------------------------
CREATE TABLE comments (
                          id          BIGINT    NOT NULL AUTO_INCREMENT,
                          content     TEXT      NOT NULL,
                          created_at  DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          author_id   BIGINT    NOT NULL,
                          article_id  BIGINT    NOT NULL,
                          PRIMARY KEY (id),
                          CONSTRAINT fk_comments_author
                              FOREIGN KEY (author_id) REFERENCES users (id),
                          CONSTRAINT fk_comments_article
                              FOREIGN KEY (article_id) REFERENCES articles (id)
                                  ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- Table subscriptions (many-to-many users <-> themes)
-- ---------------------------------------------------------
CREATE TABLE subscriptions (
                               user_id   BIGINT  NOT NULL,
                               theme_id  BIGINT  NOT NULL,
                               PRIMARY KEY (user_id, theme_id),
                               CONSTRAINT fk_subscriptions_user
                                   FOREIGN KEY (user_id) REFERENCES users (id)
                                       ON DELETE CASCADE,
                               CONSTRAINT fk_subscriptions_theme
                                   FOREIGN KEY (theme_id) REFERENCES themes (id)
                                       ON DELETE CASCADE
) ENGINE=InnoDB;

INSERT INTO themes (title, description) values ('Java', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas nec nibh sit amet ipsum ultrices mattis vel eget est. Pellentesque id mi ipsum. Etiam venenatis, nibh et pretium pharetra, augue nisi dignissim massa, a interdum est leo a mi. Mauris nec placerat arcu. Etiam volutpat dui sed tellus posuere scelerisque. Pellentesque molestie velit ut ex porttitor, sed pellentesque ante semper. Vestibulum molestie finibus mi. Quisque quis convallis nisl. Nam vel ligula massa. Sed enim dolor, ultricies ac arcu at, tincidunt ornare tortor. Praesent sapien leo, varius et interdum eget, aliquam at libero. Nunc non felis sed nunc congue luctus. Nunc condimentum sollicitudin ipsum, hendrerit ullamcorper justo tincidunt aliquet. Praesent tincidunt sed lectus at faucibus. Aliquam sit amet sapien dapibus, ultrices nulla rhoncus, ultricies risus.');
INSERT INTO themes (title, description) values ('Javascript', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas nec nibh sit amet ipsum ultrices mattis vel eget est. Pellentesque id mi ipsum. Etiam venenatis, nibh et pretium pharetra, augue nisi dignissim massa, a interdum est leo a mi. Mauris nec placerat arcu. Etiam volutpat dui sed tellus posuere scelerisque. Pellentesque molestie velit ut ex porttitor, sed pellentesque ante semper. Vestibulum molestie finibus mi. Quisque quis convallis nisl. Nam vel ligula massa. Sed enim dolor, ultricies ac arcu at, tincidunt ornare tortor. Praesent sapien leo, varius et interdum eget, aliquam at libero. Nunc non felis sed nunc congue luctus. Nunc condimentum sollicitudin ipsum, hendrerit ullamcorper justo tincidunt aliquet. Praesent tincidunt sed lectus at faucibus. Aliquam sit amet sapien dapibus, ultrices nulla rhoncus, ultricies risus.');
INSERT INTO themes (title, description) values ('Python', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas nec nibh sit amet ipsum ultrices mattis vel eget est. Pellentesque id mi ipsum. Etiam venenatis, nibh et pretium pharetra, augue nisi dignissim massa, a interdum est leo a mi. Mauris nec placerat arcu. Etiam volutpat dui sed tellus posuere scelerisque. Pellentesque molestie velit ut ex porttitor, sed pellentesque ante semper. Vestibulum molestie finibus mi. Quisque quis convallis nisl. Nam vel ligula massa. Sed enim dolor, ultricies ac arcu at, tincidunt ornare tortor. Praesent sapien leo, varius et interdum eget, aliquam at libero. Nunc non felis sed nunc congue luctus. Nunc condimentum sollicitudin ipsum, hendrerit ullamcorper justo tincidunt aliquet. Praesent tincidunt sed lectus at faucibus. Aliquam sit amet sapien dapibus, ultrices nulla rhoncus, ultricies risus.');
INSERT INTO themes (title, description) values ('C', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas nec nibh sit amet ipsum ultrices mattis vel eget est. Pellentesque id mi ipsum. Etiam venenatis, nibh et pretium pharetra, augue nisi dignissim massa, a interdum est leo a mi. Mauris nec placerat arcu. Etiam volutpat dui sed tellus posuere scelerisque. Pellentesque molestie velit ut ex porttitor, sed pellentesque ante semper. Vestibulum molestie finibus mi. Quisque quis convallis nisl. Nam vel ligula massa. Sed enim dolor, ultricies ac arcu at, tincidunt ornare tortor. Praesent sapien leo, varius et interdum eget, aliquam at libero. Nunc non felis sed nunc congue luctus. Nunc condimentum sollicitudin ipsum, hendrerit ullamcorper justo tincidunt aliquet. Praesent tincidunt sed lectus at faucibus. Aliquam sit amet sapien dapibus, ultrices nulla rhoncus, ultricies risus.');
INSERT INTO themes (title, description) values ('C++', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas nec nibh sit amet ipsum ultrices mattis vel eget est. Pellentesque id mi ipsum. Etiam venenatis, nibh et pretium pharetra, augue nisi dignissim massa, a interdum est leo a mi. Mauris nec placerat arcu. Etiam volutpat dui sed tellus posuere scelerisque. Pellentesque molestie velit ut ex porttitor, sed pellentesque ante semper. Vestibulum molestie finibus mi. Quisque quis convallis nisl. Nam vel ligula massa. Sed enim dolor, ultricies ac arcu at, tincidunt ornare tortor. Praesent sapien leo, varius et interdum eget, aliquam at libero. Nunc non felis sed nunc congue luctus. Nunc condimentum sollicitudin ipsum, hendrerit ullamcorper justo tincidunt aliquet. Praesent tincidunt sed lectus at faucibus. Aliquam sit amet sapien dapibus, ultrices nulla rhoncus, ultricies risus.');
INSERT INTO themes (title, description) values ('C#', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas nec nibh sit amet ipsum ultrices mattis vel eget est. Pellentesque id mi ipsum. Etiam venenatis, nibh et pretium pharetra, augue nisi dignissim massa, a interdum est leo a mi. Mauris nec placerat arcu. Etiam volutpat dui sed tellus posuere scelerisque. Pellentesque molestie velit ut ex porttitor, sed pellentesque ante semper. Vestibulum molestie finibus mi. Quisque quis convallis nisl. Nam vel ligula massa. Sed enim dolor, ultricies ac arcu at, tincidunt ornare tortor. Praesent sapien leo, varius et interdum eget, aliquam at libero. Nunc non felis sed nunc congue luctus. Nunc condimentum sollicitudin ipsum, hendrerit ullamcorper justo tincidunt aliquet. Praesent tincidunt sed lectus at faucibus. Aliquam sit amet sapien dapibus, ultrices nulla rhoncus, ultricies risus.');
