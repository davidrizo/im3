-- phpMyAdmin SQL Dump
-- version 4.8.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost:8889
-- Generation Time: Oct 23, 2018 at 08:33 PM
-- Server version: 5.7.23
-- PHP Version: 7.2.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `muret`
--

-- --------------------------------------------------------

--
-- Table structure for table `hibernate_sequence`
--

CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `hibernate_sequence`
--

INSERT INTO `hibernate_sequence` (`next_val`) VALUES
(1),
(1);

-- --------------------------------------------------------

--
-- Table structure for table `image`
--

CREATE TABLE `image` (
  `id` bigint(20) NOT NULL,
  `path` varchar(1024) NOT NULL,
  `project_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `image`
--

INSERT INTO `image` (`id`, `path`, `project_id`) VALUES
(1, 'https://www.researchgate.net/profile/Alexander_Pacha/publication/325871786/figure/fig1/AS:639563877871616@1529495319245/A-sample-page-of-ancient-music-written-in-mensural-notation_W640.jpg', 1),
(2, 'https://ai2-s2-public.s3.amazonaws.com/figures/2017-08-08/f0bfe867f75bf1399eaf7fd4e880bd27b7a4cc14/2-Figure1-1.png', 1);

-- --------------------------------------------------------

--
-- Table structure for table `page`
--

CREATE TABLE `page` (
  `id` bigint(20) NOT NULL,
  `bounding_box` char(32) NOT NULL COMMENT 'Format: fromX,fromY,toX,toY (all them integer)',
  `image_id` bigint(20) NOT NULL
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
  `poster_frame_path` varchar(1024) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `project`
--

INSERT INTO `project` (`id`, `name`, `created`, `path`, `lastChange`, `last_change`, `changed_by`, `created_by`, `poster_frame_path`) VALUES
(1, 'Prueba Spring', '2018-10-20 19:31:02', 'pruebaspring', '2018-10-20 19:31:02', NULL, 1, 1, 'https://content.wdl.org/19489/thumbnail/1497896523/616x510.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `region`
--

CREATE TABLE `region` (
  `id` bigint(20) NOT NULL,
  `bounding_box` char(32) NOT NULL COMMENT 'Format: fromX,fromY,toX,toY (all them integer)',
  `page_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `username` varchar(256) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id`, `username`) VALUES
(1, 'admin');

--
-- Indexes for dumped tables
--

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
-- Indexes for table `project`
--
ALTER TABLE `project`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK4xmscdxf05neuod55f1l2amfb` (`changed_by`),
  ADD KEY `FKepfrx92jw3n2lkjn2qqj1n93k` (`created_by`);

--
-- Indexes for table `region`
--
ALTER TABLE `region`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_PAGE` (`page_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `image`
--
ALTER TABLE `image`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `page`
--
ALTER TABLE `page`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `project`
--
ALTER TABLE `project`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `region`
--
ALTER TABLE `region`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

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
