package com.yazanaki.mod.data;

/**
 * Represents one active Yazanaki Empire member as returned by the API.
 * Clan abbr values: ONF, ANO, ONA, SNU, KASAII
 */
public class MemberData {

    public final String minecraftUser;
    public final String empireId;
    public final String rank;
    public final String status;
    public final String clanAbbr;
    public final String clanName;

    public MemberData(String minecraftUser, String empireId, String rank,
                      String status, String clanAbbr, String clanName) {
        this.minecraftUser = minecraftUser;
        this.empireId      = empireId;
        this.rank          = rank;
        this.status        = status;
        this.clanAbbr      = clanAbbr;
        this.clanName      = clanName;
    }

    @Override
    public String toString() {
        return "MemberData{user=" + minecraftUser + ", clan=" + clanAbbr + ", rank=" + rank + "}";
    }
}
