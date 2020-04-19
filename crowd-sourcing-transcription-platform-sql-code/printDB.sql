-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema printdb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema printdb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `printdb` DEFAULT CHARACTER SET utf32 COLLATE utf32_unicode_ci ;
USE `printdb` ;

-- -----------------------------------------------------
-- Table `printdb`.`documenttype`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`documenttype` (
  `docTypeID` INT(11) NOT NULL AUTO_INCREMENT,
  `typeDesc` VARCHAR(100) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NOT NULL,
  PRIMARY KEY (`docTypeID`),
  UNIQUE INDEX `typeDesc_UNIQUE` (`typeDesc` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`language`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`language` (
  `languageID` INT(11) NOT NULL AUTO_INCREMENT,
  `languageDesc` VARCHAR(80) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NOT NULL,
  PRIMARY KEY (`languageID`),
  UNIQUE INDEX `languageDesc_UNIQUE` (`languageDesc` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`repository`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`repository` (
  `repoID` INT(11) NOT NULL AUTO_INCREMENT,
  `repoDesc` VARCHAR(400) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `repoLOD` VARCHAR(200) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `repoURL` VARCHAR(400) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NOT NULL,
  PRIMARY KEY (`repoID`),
  UNIQUE INDEX `repoURL_UNIQUE` (`repoURL` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`document`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`document` (
  `documentID` INT(11) NOT NULL AUTO_INCREMENT,
  `importID` VARCHAR(100) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NOT NULL,
  `pdfDesc` LONGTEXT CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT NULL,
  `pdfURL` VARCHAR(400) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `internalPDFname` VARCHAR(300) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `collection` VARCHAR(300) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `abstract` LONGTEXT CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT NULL,
  `sortingDate` VARCHAR(10) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `letterDate` VARCHAR(400) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `isJulian` TINYINT(4) NULL DEFAULT '0',
  `researchNotes` LONGTEXT CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT NULL,
  `customCitation` LONGTEXT CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT NULL,
  `docTypeID` INT(11) NULL DEFAULT NULL,
  `languageID` INT(11) NULL DEFAULT NULL,
  `repositoryID` INT(11) NULL DEFAULT NULL,
  `dateAdded` DATETIME NOT NULL,
  `status` VARCHAR(30) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NOT NULL,
  `whoCheckedOut` VARCHAR(400) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  PRIMARY KEY (`documentID`),
  UNIQUE INDEX `importID_UNIQUE` (`importID` ASC),
  INDEX `FK_Document_Language_idx` (`languageID` ASC),
  INDEX `FK_DocumentType_Document_idx` (`docTypeID` ASC),
  INDEX `FK_Repository_Document_idx` (`repositoryID` ASC),
  CONSTRAINT `FK_DocumentType_Document`
    FOREIGN KEY (`docTypeID`)
    REFERENCES `printdb`.`documenttype` (`docTypeID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `FK_Language_Document`
    FOREIGN KEY (`languageID`)
    REFERENCES `printdb`.`language` (`languageID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `FK_Repository_Document`
    FOREIGN KEY (`repositoryID`)
    REFERENCES `printdb`.`repository` (`repoID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`documentedit`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`documentedit` (
  `documentEditID` INT(11) NOT NULL AUTO_INCREMENT,
  `docID` INT(11) NOT NULL,
  `userID` VARCHAR(400) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NOT NULL,
  `textType` VARCHAR(45) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `creationDate` DATETIME NOT NULL,
  `completionDate` DATETIME NULL DEFAULT NULL,
  `lastMotified` DATETIME NULL DEFAULT NULL,
  `contributed` TINYINT(4) NULL DEFAULT NULL,
  `plainText` LONGTEXT CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT NULL,
  `translationText` LONGTEXT CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT NULL,
  `documentText` LONGTEXT CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT NULL,
  PRIMARY KEY (`documentEditID`),
  INDEX `fk_DocumentEdit_Document_idx` (`docID` ASC),
  CONSTRAINT `FK_Document_d2e`
    FOREIGN KEY (`docID`)
    REFERENCES `printdb`.`document` (`documentID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`notification`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`notification` (
  `notificationID` INT(11) NOT NULL AUTO_INCREMENT,
  `notificationText` VARCHAR(200) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT NULL,
  `extraText` LONGTEXT CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT NULL,
  `notificationType` VARCHAR(100) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT NULL,
  `subjectUserID` VARCHAR(100) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT NULL,
  `date` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`notificationID`))
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`individualnotification`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`individualnotification` (
  `individualNotificationID` INT(11) NOT NULL AUTO_INCREMENT,
  `notifyWhoID` VARCHAR(400) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT NULL,
  `seen` TINYINT(4) NULL DEFAULT NULL,
  `notificationID` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`individualNotificationID`),
  INDEX `FK_IndividualNotif_Notif_idx` (`notificationID` ASC),
  CONSTRAINT `FK_IndividualNotif_Notif`
    FOREIGN KEY (`notificationID`)
    REFERENCES `printdb`.`notification` (`notificationID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`keyword`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`keyword` (
  `keywordID` INT(11) NOT NULL AUTO_INCREMENT,
  `keyword` VARCHAR(400) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NOT NULL,
  `keywordLOD` VARCHAR(100) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  PRIMARY KEY (`keywordID`),
  UNIQUE INDEX `keyword_UNIQUE` (`keyword` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`keyword2document`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`keyword2document` (
  `keyword2DocID` INT(11) NOT NULL AUTO_INCREMENT,
  `keywordID` INT(11) NOT NULL,
  `docID` INT(11) NOT NULL,
  PRIMARY KEY (`keyword2DocID`),
  INDEX `FK_Keyword_k2d_idx` (`keywordID` ASC),
  INDEX `FK_Document_k2d_idx` (`docID` ASC),
  CONSTRAINT `FK_Document_k2d`
    FOREIGN KEY (`docID`)
    REFERENCES `printdb`.`document` (`documentID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `FK_Keyword_k2d`
    FOREIGN KEY (`keywordID`)
    REFERENCES `printdb`.`keyword` (`keywordID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`libraryofcongress`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`libraryofcongress` (
  `locID` INT(11) NOT NULL AUTO_INCREMENT,
  `locSubjectHeading` VARCHAR(400) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NOT NULL,
  PRIMARY KEY (`locID`),
  UNIQUE INDEX `locSubjectHeading_UNIQUE` (`locSubjectHeading` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`libraryofcongress2doc`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`libraryofcongress2doc` (
  `loc2Doc` INT(11) NOT NULL AUTO_INCREMENT,
  `locID` INT(11) NOT NULL,
  `docID` INT(11) NOT NULL,
  PRIMARY KEY (`loc2Doc`),
  INDEX `FK_LibraryOfCongress_loc2Doc_idx` (`locID` ASC),
  INDEX `FK_Document_loc2Doc_idx` (`docID` ASC),
  CONSTRAINT `FK_Document_loc2Doc`
    FOREIGN KEY (`docID`)
    REFERENCES `printdb`.`document` (`documentID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `FK_LibraryOfCongress_loc2Doc`
    FOREIGN KEY (`locID`)
    REFERENCES `printdb`.`libraryofcongress` (`locID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`organization`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`organization` (
  `organizationID` INT(11) NOT NULL AUTO_INCREMENT,
  `organizationName` VARCHAR(500) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NOT NULL,
  `formationDate` VARCHAR(400) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `dissolutionDate` VARCHAR(400) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `organizationLOD` VARCHAR(100) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  PRIMARY KEY (`organizationID`),
  UNIQUE INDEX `organizationName_UNIQUE` (`organizationName` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`role` (
  `roleID` INT(11) NOT NULL AUTO_INCREMENT,
  `roleDesc` VARCHAR(100) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NOT NULL,
  PRIMARY KEY (`roleID`),
  UNIQUE INDEX `roleDesc_UNIQUE` (`roleDesc` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`organization2document`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`organization2document` (
  `organization2DocumentID` INT(11) NOT NULL AUTO_INCREMENT,
  `organizationID` INT(11) NOT NULL,
  `docID` INT(11) NOT NULL,
  `roleID` INT(11) NOT NULL,
  PRIMARY KEY (`organization2DocumentID`),
  INDEX `FK_Organization_o2d_idx` (`organizationID` ASC),
  INDEX `FK_o2d_Document_idx` (`docID` ASC),
  INDEX `FK_o2d_Role_idx` (`roleID` ASC),
  CONSTRAINT `FK_Organization_o2d`
    FOREIGN KEY (`organizationID`)
    REFERENCES `printdb`.`organization` (`organizationID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `FK_o2d_Document`
    FOREIGN KEY (`docID`)
    REFERENCES `printdb`.`document` (`documentID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `FK_o2d_Role`
    FOREIGN KEY (`roleID`)
    REFERENCES `printdb`.`role` (`roleID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`religion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`religion` (
  `religionID` INT(11) NOT NULL AUTO_INCREMENT,
  `religionDesc` VARCHAR(400) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NOT NULL,
  PRIMARY KEY (`religionID`),
  UNIQUE INDEX `religionDesc_UNIQUE` (`religionDesc` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`organization2religion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`organization2religion` (
  `organization2ReligionID` INT(11) NOT NULL AUTO_INCREMENT,
  `organizationID` INT(11) NOT NULL,
  `religionID` INT(11) NOT NULL,
  `dateSpan` VARCHAR(400) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  PRIMARY KEY (`organization2ReligionID`),
  INDEX `FK_Organization_o2r_idx` (`organizationID` ASC),
  INDEX `FK_Religion_o2r_idx` (`religionID` ASC),
  CONSTRAINT `FK_Organization_o2r`
    FOREIGN KEY (`organizationID`)
    REFERENCES `printdb`.`organization` (`organizationID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `FK_Religion_o2r`
    FOREIGN KEY (`religionID`)
    REFERENCES `printdb`.`religion` (`religionID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`person`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`person` (
  `personID` INT(11) NOT NULL AUTO_INCREMENT,
  `firstName` VARCHAR(400) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `middleName` VARCHAR(400) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `lastName` VARCHAR(400) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `prefix` VARCHAR(100) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `suffix` VARCHAR(100) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `biography` LONGTEXT CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT NULL,
  `gender` CHAR(1) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NOT NULL DEFAULT 'U',
  `birthDate` VARCHAR(400) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `deathDate` VARCHAR(400) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `occupation` VARCHAR(500) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `personLOD` VARCHAR(100) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NOT NULL,
  PRIMARY KEY (`personID`),
  UNIQUE INDEX `personLOD_UNIQUE` (`personLOD` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`person2document`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`person2document` (
  `person2Document` INT(11) NOT NULL AUTO_INCREMENT,
  `personID` INT(11) NOT NULL,
  `docID` INT(11) NOT NULL,
  `roleID` INT(11) NOT NULL,
  PRIMARY KEY (`person2Document`),
  INDEX `FK_Person_p2d_idx` (`personID` ASC),
  INDEX `FK_p2d_Document_idx` (`docID` ASC),
  INDEX `FK_p2d_Role_idx` (`roleID` ASC),
  CONSTRAINT `FK_Document_p2d`
    FOREIGN KEY (`docID`)
    REFERENCES `printdb`.`document` (`documentID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `FK_Person_p2d`
    FOREIGN KEY (`personID`)
    REFERENCES `printdb`.`person` (`personID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `FK_Role_p2d`
    FOREIGN KEY (`roleID`)
    REFERENCES `printdb`.`role` (`roleID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`person2religion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`person2religion` (
  `person2ReligionID` INT(11) NOT NULL AUTO_INCREMENT,
  `personID` INT(11) NOT NULL,
  `religionID` INT(11) NOT NULL,
  `dateSpan` VARCHAR(400) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  PRIMARY KEY (`person2ReligionID`),
  INDEX `FK_p2r_Person_idx` (`personID` ASC),
  INDEX `FK_Religion_p2r_idx` (`religionID` ASC),
  CONSTRAINT `FK_Person_p2r`
    FOREIGN KEY (`personID`)
    REFERENCES `printdb`.`person` (`personID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `FK_Religion_p2r`
    FOREIGN KEY (`religionID`)
    REFERENCES `printdb`.`religion` (`religionID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`place`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`place` (
  `placeID` INT(11) NOT NULL AUTO_INCREMENT,
  `placeLat` DOUBLE NOT NULL,
  `placeLong` DOUBLE NOT NULL,
  `placeCountry` VARCHAR(500) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `placeStateProv` VARCHAR(500) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `placeCounty` VARCHAR(500) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `placeTownCity` VARCHAR(500) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `placeDesc` VARCHAR(500) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  `placeLOD` VARCHAR(500) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT '',
  PRIMARY KEY (`placeID`))
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`place2document`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`place2document` (
  `place2DocID` INT(11) NOT NULL AUTO_INCREMENT,
  `placeID` INT(11) NOT NULL,
  `docID` INT(11) NOT NULL,
  `roleID` INT(11) NOT NULL,
  PRIMARY KEY (`place2DocID`),
  INDEX `FK_Place_pl2d_idx` (`placeID` ASC),
  INDEX `FK_pl2d_Document_idx` (`docID` ASC),
  INDEX `FK_pl2d_Role_idx` (`roleID` ASC),
  CONSTRAINT `FK_Document_pl2d`
    FOREIGN KEY (`docID`)
    REFERENCES `printdb`.`document` (`documentID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `FK_Place_pl2d`
    FOREIGN KEY (`placeID`)
    REFERENCES `printdb`.`place` (`placeID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `FK_Role_pl2d`
    FOREIGN KEY (`roleID`)
    REFERENCES `printdb`.`role` (`roleID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`relatedletters`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`relatedletters` (
  `relatedLettersID` INT(11) NOT NULL AUTO_INCREMENT,
  `documentID` INT(11) NOT NULL,
  `relatedLetterID` INT(11) NOT NULL,
  PRIMARY KEY (`relatedLettersID`),
  INDEX `FK_doc1Related_idx` (`documentID` ASC),
  INDEX `FK_Document_RelatedLetter_idx` (`relatedLetterID` ASC),
  CONSTRAINT `FK_Document_Doc2Relate`
    FOREIGN KEY (`documentID`)
    REFERENCES `printdb`.`document` (`documentID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `FK_Document_RelatedLetter`
    FOREIGN KEY (`relatedLetterID`)
    REFERENCES `printdb`.`document` (`documentID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 0
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


-- -----------------------------------------------------
-- Table `printdb`.`request`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `printdb`.`request` (
  `requestID` INT(11) NOT NULL AUTO_INCREMENT,
  `requester` VARCHAR(200) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT NULL,
  `roleRequested` VARCHAR(20) CHARACTER SET 'utf32' COLLATE 'utf32_unicode_ci' NULL DEFAULT NULL,
  `granted` TINYINT(4) NULL DEFAULT NULL,
  `dateRequested` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`requestID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf32
COLLATE = utf32_unicode_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
