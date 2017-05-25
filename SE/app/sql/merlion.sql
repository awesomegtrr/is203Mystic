-- phpMyAdmin SQL Dump
-- version 4.6.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Oct 28, 2016 at 01:28 AM
-- Server version: 5.6.31
-- PHP Version: 5.6.24

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `merlion`
--
CREATE DATABASE IF NOT EXISTS `merlion` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `merlion`;

-- --------------------------------------------------------

--
-- Table structure for table `bid`
--

CREATE TABLE `bid` (
  `userid` varchar(128) NOT NULL,
  `amount` float NOT NULL,
  `code` varchar(10) NOT NULL,
  `section` varchar(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `course`
--

CREATE TABLE `course` (
  `course` varchar(10) NOT NULL,
  `school` varchar(100) NOT NULL,
  `title` varchar(100) NOT NULL,
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `examdate` date NOT NULL,
  `examstart` time NOT NULL,
  `examend` time NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `course_completed`
--

CREATE TABLE `course_completed` (
  `userid` varchar(128) NOT NULL,
  `code` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `prerequisite`
--

CREATE TABLE `prerequisite` (
  `course` varchar(10) NOT NULL,
  `prerequisite` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `section`
--

CREATE TABLE `section` (
  `course` varchar(10) NOT NULL,
  `section` varchar(3) NOT NULL,
  `day` int(11) NOT NULL,
  `start` time NOT NULL,
  `end` time NOT NULL,
  `instructor` varchar(100) NOT NULL,
  `venue` varchar(100) NOT NULL,
  `size` int(11) NOT NULL,
  `minPrice` float NOT NULL DEFAULT '10'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `section_student`
--

CREATE TABLE `section_student` (
  `userid` varchar(128) NOT NULL,
  `course` varchar(10) NOT NULL,
  `section` varchar(3) NOT NULL,
  `amount` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE `student` (
  `userid` varchar(128) NOT NULL,
  `password` varchar(128) NOT NULL,
  `name` varchar(100) NOT NULL,
  `school` varchar(100) NOT NULL,
  `edollar` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bid`
--
ALTER TABLE `bid`
  ADD PRIMARY KEY (`userid`,`code`,`section`),
  ADD KEY `code` (`code`,`section`);

--
-- Indexes for table `course`
--
ALTER TABLE `course`
  ADD PRIMARY KEY (`course`);

--
-- Indexes for table `course_completed`
--
ALTER TABLE `course_completed`
  ADD PRIMARY KEY (`userid`,`code`),
  ADD KEY `code` (`code`);

--
-- Indexes for table `prerequisite`
--
ALTER TABLE `prerequisite`
  ADD PRIMARY KEY (`course`,`prerequisite`),
  ADD KEY `prerequisite` (`prerequisite`);

--
-- Indexes for table `section`
--
ALTER TABLE `section`
  ADD PRIMARY KEY (`course`,`section`);

--
-- Indexes for table `section_student`
--
ALTER TABLE `section_student`
  ADD PRIMARY KEY (`userid`,`course`,`section`),
  ADD KEY `course` (`course`,`section`);

--
-- Indexes for table `student`
--
ALTER TABLE `student`
  ADD PRIMARY KEY (`userid`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `bid`
--
ALTER TABLE `bid`
  ADD CONSTRAINT `bid_ibfk_1` FOREIGN KEY (`code`,`section`) REFERENCES `section` (`course`, `section`),
  ADD CONSTRAINT `bid_ibfk_2` FOREIGN KEY (`userid`) REFERENCES `student` (`userid`);

--
-- Constraints for table `course_completed`
--
ALTER TABLE `course_completed`
  ADD CONSTRAINT `course_completed_ibfk_1` FOREIGN KEY (`code`) REFERENCES `course` (`course`),
  ADD CONSTRAINT `course_completed_ibfk_2` FOREIGN KEY (`userid`) REFERENCES `student` (`userid`);

--
-- Constraints for table `prerequisite`
--
ALTER TABLE `prerequisite`
  ADD CONSTRAINT `prerequisite_ibfk_1` FOREIGN KEY (`course`) REFERENCES `course` (`course`),
  ADD CONSTRAINT `prerequisite_ibfk_2` FOREIGN KEY (`prerequisite`) REFERENCES `course` (`course`);

--
-- Constraints for table `section`
--
ALTER TABLE `section`
  ADD CONSTRAINT `fk_1` FOREIGN KEY (`course`) REFERENCES `course` (`course`);

--
-- Constraints for table `section_student`
--
ALTER TABLE `section_student`
  ADD CONSTRAINT `section_student_ibfk_1` FOREIGN KEY (`course`,`section`) REFERENCES `section` (`course`, `section`),
  ADD CONSTRAINT `section_student_ibfk_2` FOREIGN KEY (`userid`) REFERENCES `student` (`userid`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
