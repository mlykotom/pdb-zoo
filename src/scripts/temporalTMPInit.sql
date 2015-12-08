DROP SEQUENCE Employees_seq;
DROP SEQUENCE Employees_Shift_seq;

DROP TABLE Employees;

DROP TABLE Employees_Shift;

CREATE TABLE Employees (
  ID INT NOT NULL,
  Name VARCHAR(255) NOT NULL,
  Surname VARCHAR (255) NOT NULL,
  PRIMARY KEY (ID)
);

CREATE TABLE Employees_Shift (
  ID INT NOT NULL,
  EmplID INT NOT NULL,
  Location INT,
  dFrom DATE NOT NULL,
  dTo DATE NOT NULL,
  PRIMARY KEY (ID),
  FOREIGN KEY (Location) REFERENCES Spatial_Objects (ID),
  FOREIGN KEY (EmplID) REFERENCES Employees (ID)
);

CREATE SEQUENCE Employees_seq;
CREATE OR REPLACE TRIGGER Employees_bir
BEFORE INSERT ON Employees
FOR EACH ROW
  BEGIN
    SELECT Employees_seq.NEXTVAL
    INTO :new.id
    FROM dual;
  END;
/

CREATE SEQUENCE Employees_Shift_seq;
CREATE OR REPLACE TRIGGER Employees_Shift_bir
BEFORE INSERT ON Employees_Shift
FOR EACH ROW
  BEGIN
    SELECT Employees_Shift_seq.NEXTVAL
    INTO :new.id
    FROM dual;
  END;
/

INSERT INTO Employees_Shift ( EmplId, Location, skuska, dFrom, dTo ) VALUES (1, 2,'slovo', '01-Jan-2000', '01-Jan-2500');
INSERT INTO Employees_Shift ( EmplId, Location, skuska, dFrom, dTo ) VALUES (2, 2,'slovo', '01-Jan-1999', '01-Jan-2014');
INSERT INTO Employees_Shift ( EmplId, Location, skuska, dFrom, dTo ) VALUES (2, 7,'slovo', '01-Jan-2014', '01-Jan-2500');
INSERT INTO Employees_Shift ( EmplId, Location, skuska, dFrom, dTo ) VALUES (3, 7,'slovo', '01-Jan-2000', '01-Jan-2500');
INSERT INTO Employees_Shift ( EmplId, Location, skuska, dFrom, dTo ) VALUES (4, 2,'slovo', '01-Jan-2000', '01-Jan-2500');

INSERT INTO Employees (Name, Surname) VALUES ('Jack', 'Daniels');
INSERT INTO Employees (Name, Surname) VALUES ('Jarko', 'Mustafa');
INSERT INTO Employees (Name, Surname) VALUES ('Pa', 'Pi');
INSERT INTO Employees (Name, Surname) VALUES ('Radost', 'Zit');
INSERT INTO Employees (Name, Surname) VALUES ('John', 'McClane');

INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (1, 2, '01-Jan-2000', '01-Jan-2500');
INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (2, 2, '01-Jan-1999', '01-Jan-2014');
INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (2, 7, '01-Jan-2014', '01-Jan-2500');
INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (3, 7, '01-Jan-2000', '01-Jan-2500');
INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (4, 2, '01-Jan-2000', '01-Jan-2500');

commit

SELECT e.*, s.Location, s.dFrom, s.dTo  FROM Employees e
  LEFT JOIN Employees_Shift s on e.ID = s.EmplID
WHERE s.dFrom <= dateToShow and s.dTo >= dateToShow


SELECT * FROM Employees

SELECT * FROM Employees_Shift

SELECT e.ID, e.Name, e.Surname, Location FROM EMPLOYEES e JOIN Employees_Shift s ON e.ID = s.EmplId

SELECT * FROM Employees_Shift