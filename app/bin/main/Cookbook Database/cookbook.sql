-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: cookbook
-- ------------------------------------------------------
-- Server version	8.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `commentId` int NOT NULL AUTO_INCREMENT,
  `recipeId` int NOT NULL,
  `userId` int NOT NULL,
  `content` varchar(100) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  PRIMARY KEY (`commentId`),
  UNIQUE KEY `unique_commentId` (`commentId`),
  KEY `userId_fk_comment` (`userId`),
  KEY `recipeId_fk` (`recipeId`),
  CONSTRAINT `recipeId_fk` FOREIGN KEY (`recipeId`) REFERENCES `recipe` (`recipeId`),
  CONSTRAINT `userId_fk_comment` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (14,98,16,'this was perfect!',NULL);
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorite`
--

DROP TABLE IF EXISTS `favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorite` (
  `userId` int NOT NULL,
  `recipeId` int NOT NULL,
  PRIMARY KEY (`userId`,`recipeId`),
  KEY `recipeId` (`recipeId`),
  CONSTRAINT `recipeId_FK_fav` FOREIGN KEY (`recipeId`) REFERENCES `recipe` (`recipeId`),
  CONSTRAINT `userId_FK_fav` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorite`
--

LOCK TABLES `favorite` WRITE;
/*!40000 ALTER TABLE `favorite` DISABLE KEYS */;
INSERT INTO `favorite` VALUES (16,91),(16,93),(16,96),(16,98),(16,99);
/*!40000 ALTER TABLE `favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fridge`
--

DROP TABLE IF EXISTS `fridge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fridge` (
  `userId` int NOT NULL,
  `ingredientId` int NOT NULL,
  `quantity` int DEFAULT NULL,
  `unitId` int DEFAULT NULL,
  PRIMARY KEY (`userId`,`ingredientId`),
  KEY `fridge_ingredientid_idx` (`ingredientId`),
  KEY `fk_unitId` (`unitId`),
  CONSTRAINT `fk_unitId` FOREIGN KEY (`unitId`) REFERENCES `unit` (`unitId`),
  CONSTRAINT `fridge_ingredientid` FOREIGN KEY (`ingredientId`) REFERENCES `ingredient` (`ingredientId`),
  CONSTRAINT `fridge_userid` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fridge`
--

LOCK TABLES `fridge` WRITE;
/*!40000 ALTER TABLE `fridge` DISABLE KEYS */;
INSERT INTO `fridge` VALUES (16,90,2,102),(16,111,1,102),(16,118,100,101);
/*!40000 ALTER TABLE `fridge` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `images`
--

DROP TABLE IF EXISTS `images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `images` (
  `imageId` int NOT NULL AUTO_INCREMENT,
  `imageName` varchar(100) DEFAULT NULL,
  `imageData` longblob,
  `recipeId` int DEFAULT NULL,
  `userId` int DEFAULT NULL,
  PRIMARY KEY (`imageId`),
  KEY `recipeId` (`recipeId`),
  CONSTRAINT `images_ibfk_1` FOREIGN KEY (`recipeId`) REFERENCES `recipe` (`recipeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `images`
--

LOCK TABLES `images` WRITE;
/*!40000 ALTER TABLE `images` DISABLE KEYS */;
/*!40000 ALTER TABLE `images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingredient`
--

DROP TABLE IF EXISTS `ingredient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ingredient` (
  `ingredientId` int NOT NULL AUTO_INCREMENT,
  `ingredientName` varchar(50) NOT NULL,
  `density` double DEFAULT NULL,
  PRIMARY KEY (`ingredientId`),
  UNIQUE KEY `ingredient_UNIQUE` (`ingredientId`),
  UNIQUE KEY `ingredientName_UNIQUE` (`ingredientName`)
) ENGINE=InnoDB AUTO_INCREMENT=129 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ingredient`
--

LOCK TABLES `ingredient` WRITE;
/*!40000 ALTER TABLE `ingredient` DISABLE KEYS */;
INSERT INTO `ingredient` VALUES (65,'Garlic',NULL),(66,'Tomato',NULL),(68,'bamboo',NULL),(69,'bannana ',NULL),(77,'Potato',NULL),(78,'Rice',NULL),(84,'Beans',NULL),(85,'Zaffran',NULL),(90,'Ground beef',NULL),(92,'Breadcrumbs',NULL),(93,'Milk',NULL),(94,'Spaghetti',NULL),(95,'Guanciale',NULL),(96,'Pecorino Romano',NULL),(98,'Pasta water',NULL),(99,'Pizza dough',NULL),(100,'Marinara sauce',NULL),(101,'Cheese',NULL),(103,'Crushed tomatoes',NULL),(106,'Onion',NULL),(107,'Red wine',NULL),(108,'Heavy cream',NULL),(109,'Tomato paste',NULL),(111,'Bacon',NULL),(112,'Mayonnaise',NULL),(113,'Cucumber',NULL),(114,'Cherry tomatoes',NULL),(115,'Egg',NULL),(116,'Salmon',NULL),(118,'Dill',NULL),(119,'Lemon',NULL),(121,'Basil',NULL),(123,'Meat',NULL),(126,'saffron',NULL);
/*!40000 ALTER TABLE `ingredient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mealtime`
--

DROP TABLE IF EXISTS `mealtime`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mealtime` (
  `mealTimeName` varchar(45) NOT NULL,
  PRIMARY KEY (`mealTimeName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mealtime`
--

LOCK TABLES `mealtime` WRITE;
/*!40000 ALTER TABLE `mealtime` DISABLE KEYS */;
INSERT INTO `mealtime` VALUES ('Breakfast'),('Dinner'),('Lunch');
/*!40000 ALTER TABLE `mealtime` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `menu`
--

DROP TABLE IF EXISTS `menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu` (
  `userId` int NOT NULL,
  `date` date NOT NULL,
  `mealTimeName` varchar(45) NOT NULL,
  `recipeId` int NOT NULL,
  PRIMARY KEY (`userId`,`date`,`mealTimeName`,`recipeId`),
  KEY `mealTimeName` (`mealTimeName`),
  KEY `recipeId_idx` (`recipeId`),
  CONSTRAINT `mealTimeName` FOREIGN KEY (`mealTimeName`) REFERENCES `mealtime` (`mealTimeName`),
  CONSTRAINT `recipeId` FOREIGN KEY (`recipeId`) REFERENCES `recipe` (`recipeId`),
  CONSTRAINT `userId` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu`
--

LOCK TABLES `menu` WRITE;
/*!40000 ALTER TABLE `menu` DISABLE KEYS */;
INSERT INTO `menu` VALUES (16,'2024-05-26','Dinner',91),(16,'2024-06-02','Dinner',91),(16,'2024-05-29','Lunch',95),(16,'2024-10-01','Lunch',99);
/*!40000 ALTER TABLE `menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `senderUserId` int NOT NULL,
  `recieverUserId` int NOT NULL,
  `recipeId` int NOT NULL,
  `text` varchar(200) NOT NULL,
  PRIMARY KEY (`senderUserId`,`recieverUserId`,`recipeId`),
  KEY `senderUserId` (`senderUserId`) /*!80000 INVISIBLE */,
  KEY `receiverUserId` (`recieverUserId`),
  KEY `recipeId` (`recipeId`),
  CONSTRAINT `mes_recieverId` FOREIGN KEY (`recieverUserId`) REFERENCES `user` (`userId`),
  CONSTRAINT `mes_recipeId` FOREIGN KEY (`recipeId`) REFERENCES `recipe` (`recipeId`),
  CONSTRAINT `mes_senderId` FOREIGN KEY (`senderUserId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (16,18,99,'it is soooo sweeet'),(21,16,91,'Tommorow night together?'),(21,16,93,'Mamma mia!'),(21,16,94,'Dude this is good!!!'),(22,16,96,'japanese foooooooood');
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ratings`
--

DROP TABLE IF EXISTS `ratings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ratings` (
  `userId` int NOT NULL,
  `recipeId` int NOT NULL,
  `rate` int NOT NULL,
  PRIMARY KEY (`userId`,`recipeId`),
  KEY `recipeId` (`recipeId`),
  CONSTRAINT `ratings_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `ratings_ibfk_2` FOREIGN KEY (`recipeId`) REFERENCES `recipe` (`recipeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ratings`
--

LOCK TABLES `ratings` WRITE;
/*!40000 ALTER TABLE `ratings` DISABLE KEYS */;
/*!40000 ALTER TABLE `ratings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recipe`
--

DROP TABLE IF EXISTS `recipe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recipe` (
  `recipeId` int NOT NULL AUTO_INCREMENT,
  `userId` int NOT NULL,
  `recipeName` varchar(45) NOT NULL,
  `shortDescription` varchar(50) NOT NULL,
  `fullDescription` varchar(1000) NOT NULL,
  `portion` int NOT NULL,
  `imageURL` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`recipeId`,`userId`),
  UNIQUE KEY `recipeid_UNIQUE` (`recipeId`),
  UNIQUE KEY `recipeName_UNIQUE` (`recipeName`),
  KEY `userId_fk` (`userId`),
  CONSTRAINT `userId_fk` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recipe`
--

LOCK TABLES `recipe` WRITE;
/*!40000 ALTER TABLE `recipe` DISABLE KEYS */;
INSERT INTO `recipe` VALUES (91,16,'Meatloaf','A swedish meatloaf','How to make a traditional swedish meatloaf.',4,'/images/MSL-RM-315823-meatloaf-hero-1223-2261c5a72c5743c287407e7ffb077399.jpg'),(93,16,'Carbonara','A classic carbonara','Cook the pasta, fry the guanciale, crack the eggs in a bowl with grated pecorino, pasta water and mix\ntogether. Mix the pasta together with the mixture in a frying pan.',2,'/images/pasta-carbonara-spaghetti-with-bacon-parsel-parmesan-cheese-pasta-carbonara-black-plate-with-parmesa.jpg'),(94,16,'Pizza','Classic pizza','Roll out the dough, spread out the tomato sauce, add topping of choice and spread the over the grated cheese.',1,'/images/pizza-with-pepperoni-it-bunch-other-ingredients-background_581634-15605.jpg'),(95,16,'Lasagna','Easy lasagna','Grate garlic, and chop the onions and fry in a pan. Fry the ground meat and add in the crushed tomatoes \nand make a simple meats sauce. Layer the meat sauce with the lasagna plates and the bechamelle sauce. \nLet cook in the oven for 30 minutes.',4,'/images/smoked-lasagna.jpg'),(96,16,'Potato salad','Potato salad great for bbq','Boil the potatoes, fry the bacon and slice the cucumbers. Thinly slice the onions and fry the in the leftover fat \nof the bacon. When everything has cooled, mix all together with mayo, season with salt and pepper.',4,'/images/Japanese-Potato-Salad-9250-I-1.jpg'),(97,18,'Salmon with potatoes','Simple salmon dish','Cut up the salmon into fillets and packets of aluminium foil. Season the fish with salt, pepper, dill and lemon. \nServe with cooked or oven baked potatoes.',2,'/images/plate-salmon-with-mashed-potatoes-sprig-rosemary_919582-1186.jpg'),(98,16,'Ravioli','An Italian dish','Ravioli are a type of stuffed pasta comprising a filling enveloped in thin pasta dough.\nUsually served in broth or with a sauce, they originated as a traditional food in Italian cuisine. \nRavioli are commonly square,\nthough other forms are also used, including circular and semi-circular.',2,'/images/b092f713-378c-49b5-9627-a2d4ce5546a1.jpg'),(99,16,'Sholeh Zard','Persian dessert','very tasty',4,'/images/d04f38be-266d-47b5-9f04-6ae87bd2f8ad.gif');
/*!40000 ALTER TABLE `recipe` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recipe_ingredient`
--

DROP TABLE IF EXISTS `recipe_ingredient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recipe_ingredient` (
  `ingredientId` int NOT NULL,
  `recipeId` int NOT NULL,
  `unitId` int NOT NULL,
  `quantity` double NOT NULL,
  PRIMARY KEY (`ingredientId`,`recipeId`),
  KEY `recipeId_fk_ri` (`recipeId`),
  KEY `unitId_fk_idx` (`unitId`),
  CONSTRAINT `ingredientid_fk` FOREIGN KEY (`ingredientId`) REFERENCES `ingredient` (`ingredientId`),
  CONSTRAINT `recipeId_fk_ri` FOREIGN KEY (`recipeId`) REFERENCES `recipe` (`recipeId`),
  CONSTRAINT `unitId_fk` FOREIGN KEY (`unitId`) REFERENCES `unit` (`unitId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recipe_ingredient`
--

LOCK TABLES `recipe_ingredient` WRITE;
/*!40000 ALTER TABLE `recipe_ingredient` DISABLE KEYS */;
INSERT INTO `recipe_ingredient` VALUES (65,91,96,3),(65,95,96,2),(66,98,102,2),(77,96,101,800),(77,97,101,200),(78,99,102,2),(90,91,101,500),(90,95,101,800),(92,91,114,1),(93,91,114,2),(94,93,101,200),(95,93,101,300),(96,93,101,100),(96,95,101,100),(98,93,114,1),(98,98,101,500),(98,99,113,1),(99,94,96,1),(100,94,114,2),(101,94,114,4),(103,95,101,250),(106,95,96,1),(107,95,114,3),(108,95,114,2),(111,96,101,300),(112,96,114,3),(113,96,96,1),(114,96,96,30),(115,96,96,3),(116,97,101,400),(118,97,111,5),(119,97,96,1),(121,98,101,200),(123,98,101,500),(126,99,101,1);
/*!40000 ALTER TABLE `recipe_ingredient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recipe_rate`
--

DROP TABLE IF EXISTS `recipe_rate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recipe_rate` (
  `userId` int NOT NULL,
  `recipeId` int NOT NULL,
  `rate` int DEFAULT NULL,
  PRIMARY KEY (`userId`,`recipeId`),
  UNIQUE KEY `userId_UNIQUE` (`userId`),
  UNIQUE KEY `recipeId_UNIQUE` (`recipeId`),
  CONSTRAINT `recipe_idfk` FOREIGN KEY (`recipeId`) REFERENCES `recipe` (`recipeId`),
  CONSTRAINT `user_idfk` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recipe_rate`
--

LOCK TABLES `recipe_rate` WRITE;
/*!40000 ALTER TABLE `recipe_rate` DISABLE KEYS */;
/*!40000 ALTER TABLE `recipe_rate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recipe_tag`
--

DROP TABLE IF EXISTS `recipe_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recipe_tag` (
  `recipeId` int NOT NULL,
  `tagId` int NOT NULL,
  PRIMARY KEY (`recipeId`,`tagId`),
  KEY `tagid_fk` (`tagId`),
  CONSTRAINT `recipeid_fk_tag` FOREIGN KEY (`recipeId`) REFERENCES `recipe` (`recipeId`),
  CONSTRAINT `tagid_fk` FOREIGN KEY (`tagId`) REFERENCES `tag` (`tagId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recipe_tag`
--

LOCK TABLES `recipe_tag` WRITE;
/*!40000 ALTER TABLE `recipe_tag` DISABLE KEYS */;
INSERT INTO `recipe_tag` VALUES (91,58),(93,58),(94,58),(95,58),(97,58),(98,58),(91,59),(96,60),(93,73),(94,73),(95,73),(98,73),(96,81),(96,82),(97,84),(98,87),(99,88),(99,89),(99,90);
/*!40000 ALTER TABLE `recipe_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recipeimage`
--

DROP TABLE IF EXISTS `recipeimage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recipeimage` (
  `recipeID` int NOT NULL,
  `image` blob,
  PRIMARY KEY (`recipeID`),
  CONSTRAINT `recipe_image_fk` FOREIGN KEY (`recipeID`) REFERENCES `recipe` (`recipeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recipeimage`
--

LOCK TABLES `recipeimage` WRITE;
/*!40000 ALTER TABLE `recipeimage` DISABLE KEYS */;
/*!40000 ALTER TABLE `recipeimage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shopping_list`
--

DROP TABLE IF EXISTS `shopping_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shopping_list` (
  `userId` int NOT NULL,
  `ingredientId` int NOT NULL,
  `unitId` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`userId`,`ingredientId`,`unitId`),
  KEY `ingredientId` (`ingredientId`),
  KEY `unitId` (`unitId`),
  CONSTRAINT `shopping_list_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `shopping_list_ibfk_2` FOREIGN KEY (`ingredientId`) REFERENCES `ingredient` (`ingredientId`),
  CONSTRAINT `shopping_list_ibfk_3` FOREIGN KEY (`unitId`) REFERENCES `unit` (`unitId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shopping_list`
--

LOCK TABLES `shopping_list` WRITE;
/*!40000 ALTER TABLE `shopping_list` DISABLE KEYS */;
INSERT INTO `shopping_list` VALUES (16,65,96,5),(16,78,102,2),(16,90,102,3),(16,92,114,1),(16,93,114,2),(16,96,101,100),(16,98,113,1),(16,103,101,250),(16,106,96,1),(16,107,114,3),(16,108,114,2),(16,126,101,1);
/*!40000 ALTER TABLE `shopping_list` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shoppinglist`
--

DROP TABLE IF EXISTS `shoppinglist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shoppinglist` (
  `userId` int NOT NULL,
  `recipeId` int NOT NULL,
  `day` date NOT NULL,
  `time` time NOT NULL,
  PRIMARY KEY (`userId`,`recipeId`),
  KEY `shop_recipeId_idx` (`recipeId`),
  CONSTRAINT `shop_recipeId` FOREIGN KEY (`recipeId`) REFERENCES `recipe` (`recipeId`),
  CONSTRAINT `shop_userId` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shoppinglist`
--

LOCK TABLES `shoppinglist` WRITE;
/*!40000 ALTER TABLE `shoppinglist` DISABLE KEYS */;
/*!40000 ALTER TABLE `shoppinglist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `tagId` int NOT NULL AUTO_INCREMENT,
  `tagName` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`tagId`),
  UNIQUE KEY `tagName` (`tagName`)
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
INSERT INTO `tag` VALUES (90,' '),(81,' BBQ'),(61,' Children'),(84,' Fish'),(73,' Italian'),(87,' Pasta'),(82,' Salad'),(59,' Swedish'),(89,' SweetDessert'),(48,'Hungerian'),(52,'Iraniain'),(50,'Iranian'),(57,'IT'),(58,'Main Course'),(51,'Middlerastern'),(49,'Nonvegan'),(88,'Persian'),(56,'Persian. Middlestern'),(60,'Starter'),(55,'swedes'),(53,'Swedish'),(40,'vegan'),(47,'vegeterian');
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `unit`
--

DROP TABLE IF EXISTS `unit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `unit` (
  `unitId` int NOT NULL AUTO_INCREMENT,
  `unitName` varchar(45) NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`unitId`),
  UNIQUE KEY `unitId_UNIQUE` (`unitId`),
  UNIQUE KEY `unitName_UNIQUE` (`unitName`)
) ENGINE=InnoDB AUTO_INCREMENT=156 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `unit`
--

LOCK TABLES `unit` WRITE;
/*!40000 ALTER TABLE `unit` DISABLE KEYS */;
INSERT INTO `unit` VALUES (96,'pieces',NULL),(101,'g','mass'),(102,'kg','mass'),(103,'mg','mass'),(111,'cl','volume'),(112,'ml','volume'),(113,'l','volume'),(114,'dl','volume'),(115,'hg','mass');
/*!40000 ALTER TABLE `unit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `userId` int NOT NULL AUTO_INCREMENT,
  `userName` varchar(45) NOT NULL,
  `displayName` varchar(45) NOT NULL,
  `password` varchar(255) NOT NULL,
  `admin` tinyint NOT NULL,
  `imagePath` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `idUser_UNIQUE` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (16,'test','test','$2a$10$t423U/mc29GnBuniwBqyU.W8ca73KmsTbNg2RTiiPagnty/zixTYG',1,'/images/bafe434a-a25a-4a5a-bb0a-d227cce45f79.jpg'),(17,'test','test','$2a$10$t423U/mc29GnBuniwBqyU.W8ca73KmsTbNg2RTiiPagnty/zixTYG',1,NULL),(18,'Gustav','Gustav','$2a$10$OF/9cACsj6wrsdXrnvC5FeqzuwcfwEJkWkm06blnjC6wd2MHcL6S.',0,NULL),(19,'Yasaman','Yasolin','$2a$10$jro1LzqIZEaWy3Bu2fNyQeD6d6j7kes1/XCYbfKE8jCH8W9bnbnGC',0,NULL),(20,'Raime','Rai','$2a$10$8TIcipjAIv02nz0WPNP0N.YzklIBN.JYiO2UqP7L5Ep7rTS/7Pfy6',0,NULL),(21,'yasolin','yasolin','$2a$10$wGISYU6pmtVP1Dl1vNIhU.WCst7WOpRsCpWSdOBQF4Csczhe0KnqW',0,NULL),(22,'Dan','danial','$2a$10$63rZpxxdxv/pJDB2vo4ES.DvOM9ITyRpNptbYCuKZYrkG2Kg/P2ce',0,NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_food_list`
--

DROP TABLE IF EXISTS `user_food_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_food_list` (
  `userId` int NOT NULL,
  `recipeId` int NOT NULL,
  `date` date NOT NULL,
  `meal` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`userId`,`recipeId`),
  KEY `food_recipeid_idx` (`recipeId`),
  CONSTRAINT `food_recipeid` FOREIGN KEY (`recipeId`) REFERENCES `recipe` (`recipeId`),
  CONSTRAINT `food_userid` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_food_list`
--

LOCK TABLES `user_food_list` WRITE;
/*!40000 ALTER TABLE `user_food_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_food_list` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_setting`
--

DROP TABLE IF EXISTS `user_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_setting` (
  `userId` int NOT NULL,
  `theme` varchar(45) NOT NULL,
  PRIMARY KEY (`userId`),
  CONSTRAINT `setting_userid` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_setting`
--

LOCK TABLES `user_setting` WRITE;
/*!40000 ALTER TABLE `user_setting` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_setting` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-05-29 17:42:31
