# Yazanaki Mod
The Fabric mod for the Yazanaki Empire

# Yazanaki Empire Fabric Mod — Project Overview

## Core Vision

The Yazanaki Empire Fabric Mod is a private client-side tactical and organizational system designed to improve coordination, intelligence sharing, identification, and operational efficiency for members of the Yazanaki Empire across public Minecraft SMPs.

The mod is not intended to function as a hacked client or server-side faction plugin. Instead, it acts as a persistent infrastructure layer that exists independently of any specific Minecraft server.

The long-term goal is to provide the Yazanaki Empire with:

* superior coordination
* faster communication
* tactical advantages
* identity persistence
* intelligence systems
* operational infrastructure
* organizational continuity across multiple SMPs and communities

The mod is intended to work entirely client-side so that it can function on public servers without requiring server installation.

---

# Design Philosophy

## Main Focus Areas

The mod is primarily focused on:

* tactical coordination
* military/intelligence systems
* utility/QOL systems
* stealth-focused infrastructure

The project is intentionally designed around organizational superiority rather than combat cheating or unfair gameplay automation.

The mod should avoid:

* xray systems
* combat automation
* movement cheats
* packet abuse
* unfair PvP advantages
* anti-cheat detectable features

The primary advantage provided by the mod should come from:

* information organization
* coordination efficiency
* intelligence sharing
* operational awareness
* ally identification
* reduced internal mistakes

---

# Architecture Overview

The mod is designed around multiple functional layers.

---

# Layer 1 — Public Tactical Utility Layer

This layer contains features that do not require authentication and can function using public empire metadata.

These systems are intended to:

* improve coordination
* improve identification
* reduce friendly fire
* provide tactical awareness

This layer may be available to:

* any user with the mod installed
* without requiring EmpireID authentication

## Public Metadata System

The mod will retrieve public member metadata from a remote API or database-backed endpoint.

Example data:

* Minecraft username
* UUID
* clan association
* public rank data

This allows the mod to identify empire members without requiring authentication.

Potential backend sources:

* MySQL database
* REST API
* JSON endpoint

Cached local storage may also be used for offline functionality.

---

# Clan Recognition System

The mod will identify empire members and visually distinguish them based on clan affiliation.

Planned clan colors:

* ONF → light grey
* ANO → dark red
* ONA → purple
* SNU → black
* KASAII → pink

## Visual Identification Features

Potential features include:

* colored glowing outlines
* colored nametags
* role indicators
* clan tags
* distance indicators

These visual indicators should only be visible to users running the mod.

Outsiders should not be able to identify Yazanaki members through the mod.

---

# Ally Protection System

The mod will include systems intended to reduce accidental attacks between empire members.

Since the mod is client-side only, these systems will function locally rather than through server enforcement.

Potential implementation methods:

* cancel left-click attacks on allies
* warning prompts before attacking allies
* modifier-key override for intentional attacks
* ally attack indicators

The purpose is to reduce:

* accidental team damage
* confusion during fights
* operational mistakes

---

# Tactical Overlay System

The mod may include tactical HUD systems that provide useful battlefield information.

Potential HUD data:

* nearby empire members
* nearby clan distribution
* ally count
* directional indicators
* operation markers
* threat alerts

Possible overlay modes:

* minimal
* tactical
* operation
* stealth

---

# Directional and Squad Systems

Potential features:

* arrows pointing toward nearby members
* squad distance indicators
* directional operation markers
* rally point indicators

The purpose is to improve coordination during:

* raids
* fights
* scouting
* operations
* large-scale events

---

# Layer 2 — Authentication and Identity Infrastructure

This layer is planned for later implementation.

The goal is to create a persistent empire identity system using EmpireIDs.

---

# EmpireID System

Each member of the Yazanaki Empire will have a permanent EmpireID.

Characteristics:

* permanently assigned
* linked to Discord account
* can be deactivated when removed from empire
* reactivated upon rejoining

EmpireIDs will function as the primary identity layer rather than relying on Minecraft usernames.

Minecraft usernames may change, but EmpireIDs remain persistent.

---

# Authentication System

The mod will eventually include a login/authentication system.

## Authentication Goals

Users should:

* authenticate once per device/install/account
* remain logged in between sessions
* use secure session tokens
* avoid repeated manual logins

Potential implementation:

* EmpireID
* access key/password
* session token
* device token

Potential future integration:

* Kenzai-generated access credentials
* backend verification
* permission syncing

---

# Permission and Role System

The mod is intended to support multiple permission tiers.

Potential tiers:

* public user
* authenticated member
* clan member
* officer
* high command

These permissions may control access to:

* operations
* broadcasts
* tactical systems
* intelligence systems
* officer tools
* sensitive information

---

# Layer 3 — Communication Systems

These systems are intended to improve secure empire coordination.

---

# Private Communication Systems

Potential future features:

* encrypted empire chat
* clan chat
* operation channels
* officer channels

These communications would function independently from Minecraft server chat.

Benefits:

* reduced server visibility
* stealth coordination
* cross-server communication
* centralized communication systems

---

# Alert and Broadcast Systems

High-ranking members may gain the ability to send empire-wide alerts.

Potential alerts:

* enemy spotted
* regroup
* emergency
* rally point
* operation updates

Potential notification methods:

* HUD popups
* sound alerts
* directional indicators
* tactical overlays

---

# Operation System

The mod may eventually support organized tactical operations.

Potential operation features:

* operation creation
* custom operation names
* configurable expiration times
* temporary operation markers
* operation-only communication
* member grouping

Operations may be created by:

* officers
* high command
* authorized personnel

Operation expiration should be configurable per operation.

---

# Layer 4 — Intelligence Infrastructure

The long-term vision includes empire-wide intelligence systems.

---

# Intelligence Database

Potential intelligence data:

* enemy bases
* stash locations
* threat locations
* hostile groups
* scouting reports
* screenshots
* operation notes

The purpose is to create persistent empire intelligence infrastructure.

---

# Secure Coordinate Sharing

One of the major planned features is secure internal coordinate sharing.

Potential capabilities:

* encrypted waypoints
* private stash markers
* operation coordinates
* hidden rally points

This avoids exposing sensitive information through:

* Discord
* public chat
* screenshots

---

# Threat Tracking Systems

Potential future systems:

* hostile player tracking
* enemy group recognition
* threat levels
* last-seen reports
* danger area markers

Potential uses:

* scouting
* counterintelligence
* operational awareness

---

# Layer 5 — Long-Term Infrastructure

The long-term vision extends beyond Minecraft itself.

Potential future systems:

* web dashboard
* analytics
* operation history
* member activity systems
* intelligence archives
* empire-wide infrastructure tools

These systems would allow the empire to maintain persistent organization across multiple SMPs and communities.

---

# Technical Goals

## Platform

* Fabric Mod Loader

## Backend

* MySQL database
* custom API/backend services

## Client-Side Focus

The mod is intentionally designed to function entirely client-side in order to:

* work on public SMPs
* avoid server dependencies
* preserve stealth
* maintain portability

---

# Long-Term Vision

The long-term goal of the project is to create a persistent tactical and organizational infrastructure that gives the Yazanaki Empire a substantial coordination advantage over normal SMP groups.

The mod is intended to evolve into:

* a tactical coordination system
* an intelligence-sharing platform
* an operational infrastructure layer
* a persistent empire ecosystem

rather than simply functioning as a standard Minecraft utility mod.
