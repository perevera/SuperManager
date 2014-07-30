-- CREATE TABLE Team (
--   Team_Id INTEGER  NOT NULL  ,
--   Name VARCHAR(20)      ,
-- PRIMARY KEY(Team_Id)  );


-- CREATE INDEX Team_Name ON Team (Name);

-- CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT NOT NULL," + TEAM + " TEXT NOT NULL,"
-- //                + PERCENTAGE + " REAL NOT NULL," + AVERAGE + " REAL NOT NULL," + PRICE + " INTEGER NOT NULL" + ");"

CREATE TABLE Players (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    position INTEGER NOT NULL,
    team TEXT NOT NULL,
    percentage REAL NOT NULL,
    average REAL NOT NULL,
    price INTEGER NOT NULL
--     minutes TEXT NOT NULL,
--     lastval REAL NOT NULL,
--     last3val REAL NOT NULL,
--     up15 REAL NOT NULL,
--     equal REAL NOT NULL,
--     down15 REAL NOT NULL
);

CREATE TABLE Teams (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    id INTEGER NOT NULL,
    name TEXT NOT NULL
--     points REAL NOT NULL,
--     money INTEGER NOT NULL,
--     gameday INTEGER NOT NULL
);

CREATE TABLE Players2Teams (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    url TEXT NOT NULL,
    player_name TEXT NOT NULL,
    team_id INTEGER NOT NULL,
    position INTEGER NOT NULL,
    costpurchase INTEGER NOT NULL,
    FOREIGN KEY(player_name) REFERENCES Players(name) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY(team_id) REFERENCES Teams(id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

-- CREATE INDEX Player2Team_FKIndex1 ON Player2Team (Team_Id);
-- CREATE INDEX Player2Team_FKIndex2 ON Player2Team (Player_Id);