-- phpMyAdmin SQL Dump
-- version 4.8.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost:8889
-- Generation Time: Dec 16, 2018 at 12:36 PM
-- Server version: 5.7.23
-- PHP Version: 7.2.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `muret`
--

-- --------------------------------------------------------

--
-- Table structure for table `classifier`
--

CREATE TABLE `classifier` (
  `id` int(11) NOT NULL,
  `classifier_type_id` int(11) NOT NULL,
  `value` varchar(512) NOT NULL,
  `description` varchar(512) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `classifier_type`
--

CREATE TABLE `classifier_type` (
  `id` int(11) NOT NULL,
  `name` varchar(256) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `hibernate_sequence`
--

CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `image`
--

CREATE TABLE `image` (
  `id` bigint(20) NOT NULL,
  `filename` varchar(512) NOT NULL,
  `width` int(11) NOT NULL,
  `height` int(11) NOT NULL,
  `project_id` int(11) NOT NULL,
  `comments` varchar(2048) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `page`
--

CREATE TABLE `page` (
  `id` bigint(20) NOT NULL,
  `bounding_box` varchar(255) NOT NULL COMMENT 'Format: fromX,fromY,toX,toY (all them integer)',
  `image_id` bigint(20) NOT NULL,
  `comments` varchar(2048) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `permissions`
--

CREATE TABLE `permissions` (
  `id` int(11) NOT NULL,
  `project_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `permissions` char(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `project`
--

CREATE TABLE `project` (
  `id` int(11) NOT NULL,
  `name` varchar(256) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `path` varchar(1024) NOT NULL,
  `lastChange` timestamp NULL DEFAULT NULL,
  `last_change` datetime DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `thumbnail_base64_encoding` longtext COMMENT 'Image used as thumbnail in Base64 encoding',
  `comments` varchar(2048) DEFAULT NULL,
  `images_ordering` varchar(512) DEFAULT NULL COMMENT 'Comma separated list of image ids - when an image is not present here is sorted at the end of the list',
  `notation_type` varchar(32) NOT NULL COMMENT 'See IM3 NotationType Enumerated',
  `composer` varchar(512) DEFAULT NULL,
  `manuscript_type` varchar(128) NOT NULL COMMENT 'See IMCore ManuscriptType enum'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `region`
--

CREATE TABLE `region` (
  `id` bigint(20) NOT NULL,
  `bounding_box` varchar(255) NOT NULL COMMENT 'Format: fromX,fromY,toX,toY (all them integer)',
  `page_id` bigint(20) NOT NULL,
  `comments` varchar(20148) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `symbol`
--

CREATE TABLE `symbol` (
  `id` bigint(20) NOT NULL,
  `bounding_box` varchar(255) DEFAULT NULL COMMENT 'Format: fromX,toX,fromY,toY',
  `strokes` varchar(8192) DEFAULT NULL COMMENT 'Format is application dependent',
  `agnostic_encoding` varchar(256) DEFAULT NULL,
  `region_id` bigint(20) NOT NULL,
  `comments` varchar(2048) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `username` varchar(256) NOT NULL,
  `password` varchar(256) NOT NULL,
  `email` varchar(512) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `user_classifiers`
--

CREATE TABLE `user_classifiers` (
  `id` int(11) NOT NULL,
  `classifier_type_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `classifier_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `classifier`
--
ALTER TABLE `classifier`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `ClassifierNameIDX` (`value`),
  ADD KEY `ClassifierIdx` (`classifier_type_id`);

--
-- Indexes for table `classifier_type`
--
ALTER TABLE `classifier_type`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `ClassifierTypeName` (`name`);

--
-- Indexes for table `image`
--
ALTER TABLE `image`
  ADD PRIMARY KEY (`id`),
  ADD KEY `project_id_idx` (`project_id`);

--
-- Indexes for table `page`
--
ALTER TABLE `page`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_IMAGE` (`image_id`);

--
-- Indexes for table `permissions`
--
ALTER TABLE `permissions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `project_id` (`project_id`,`user_id`),
  ADD KEY `UserProjectsProjectIdx` (`project_id`),
  ADD KEY `UserProjectsUserIdx` (`user_id`);

--
-- Indexes for table `project`
--
ALTER TABLE `project`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`),
  ADD KEY `FK4xmscdxf05neuod55f1l2amfb` (`changed_by`),
  ADD KEY `FKepfrx92jw3n2lkjn2qqj1n93k` (`created_by`);

--
-- Indexes for table `region`
--
ALTER TABLE `region`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `bounding_box` (`bounding_box`,`page_id`),
  ADD KEY `FK_PAGE` (`page_id`);

--
-- Indexes for table `symbol`
--
ALTER TABLE `symbol`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_REGION` (`region_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `username_2` (`username`);

--
-- Indexes for table `user_classifiers`
--
ALTER TABLE `user_classifiers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `classifier_type_id` (`classifier_type_id`,`user_id`),
  ADD KEY `PreferencesClassifierTypeIdx` (`classifier_type_id`),
  ADD KEY `PreferencesUserIdx` (`user_id`),
  ADD KEY `PreferencesClassifierIdx` (`classifier_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `classifier`
--
ALTER TABLE `classifier`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `classifier_type`
--
ALTER TABLE `classifier_type`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `image`
--
ALTER TABLE `image`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `page`
--
ALTER TABLE `page`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `permissions`
--
ALTER TABLE `permissions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `project`
--
ALTER TABLE `project`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `region`
--
ALTER TABLE `region`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `symbol`
--
ALTER TABLE `symbol`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user_classifiers`
--
ALTER TABLE `user_classifiers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `classifier`
--
ALTER TABLE `classifier`
  ADD CONSTRAINT `FK_CLASSIFIER` FOREIGN KEY (`classifier_type_id`) REFERENCES `classifier_type` (`id`);

--
-- Constraints for table `image`
--
ALTER TABLE `image`
  ADD CONSTRAINT `FK_PROJECTW` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`);

--
-- Constraints for table `page`
--
ALTER TABLE `page`
  ADD CONSTRAINT `FK_IMAGE` FOREIGN KEY (`image_id`) REFERENCES `image` (`id`);

--
-- Constraints for table `permissions`
--
ALTER TABLE `permissions`
  ADD CONSTRAINT `FKProject` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  ADD CONSTRAINT `FKUser` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Constraints for table `project`
--
ALTER TABLE `project`
  ADD CONSTRAINT `FK4xmscdxf05neuod55f1l2amfb` FOREIGN KEY (`changed_by`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `FKepfrx92jw3n2lkjn2qqj1n93k` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`);

--
-- Constraints for table `region`
--
ALTER TABLE `region`
  ADD CONSTRAINT `FK_PAGE` FOREIGN KEY (`page_id`) REFERENCES `page` (`id`);

--
-- Constraints for table `symbol`
--
ALTER TABLE `symbol`
  ADD CONSTRAINT `FK_REGION` FOREIGN KEY (`region_id`) REFERENCES `region` (`id`);

--
-- Constraints for table `user_classifiers`
--
ALTER TABLE `user_classifiers`
  ADD CONSTRAINT `FKClassifier` FOREIGN KEY (`classifier_id`) REFERENCES `classifier` (`id`),
  ADD CONSTRAINT `FKClassifierType` FOREIGN KEY (`classifier_type_id`) REFERENCES `classifier_type` (`id`);
