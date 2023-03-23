package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate(true)
@Table(name = "large_entity")
public class LargeEntity {

    @Id
    private int id;

    private String field01;
    private String field02;
    private String field03;
    private String field04;
    private String field05;
    private String field06;
    private String field07;
    private String field08;
    private String field09;
    private String field10;

    private int field11;
    private int field12;
    private int field13;
    private int field14;
    private int field15;
    private int field16;
    private int field17;
    private int field18;
    private int field19;
    private int field20;

    private String field21;
    private String field22;
    private String field23;
    private String field24;
    private String field25;
    private String field26;
    private String field27;
    private String field28;
    private String field29;
    private String field30;

    private String field31;
    private String field32;
    private String field33;
    private String field34;
    private String field35;
    private String field36;
    private String field37;
    private String field38;
    private String field39;
    private String field40;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getField01() {
        return field01;
    }

    public void setField01(String field01) {
        this.field01 = field01;
    }

    public String getField02() {
        return field02;
    }

    public void setField02(String field02) {
        this.field02 = field02;
    }

    public String getField03() {
        return field03;
    }

    public void setField03(String field03) {
        this.field03 = field03;
    }

    public String getField04() {
        return field04;
    }

    public void setField04(String field04) {
        this.field04 = field04;
    }

    public String getField05() {
        return field05;
    }

    public void setField05(String field05) {
        this.field05 = field05;
    }

    public String getField06() {
        return field06;
    }

    public void setField06(String field06) {
        this.field06 = field06;
    }

    public String getField07() {
        return field07;
    }

    public void setField07(String field07) {
        this.field07 = field07;
    }

    public String getField08() {
        return field08;
    }

    public void setField08(String field08) {
        this.field08 = field08;
    }

    public String getField09() {
        return field09;
    }

    public void setField09(String field09) {
        this.field09 = field09;
    }

    public String getField10() {
        return field10;
    }

    public void setField10(String field10) {
        this.field10 = field10;
    }

    public int getField11() {
        return field11;
    }

    public void setField11(int field11) {
        this.field11 = field11;
    }

    public int getField12() {
        return field12;
    }

    public void setField12(int field12) {
        this.field12 = field12;
    }

    public int getField13() {
        return field13;
    }

    public void setField13(int field13) {
        this.field13 = field13;
    }

    public int getField14() {
        return field14;
    }

    public void setField14(int field14) {
        this.field14 = field14;
    }

    public int getField15() {
        return field15;
    }

    public void setField15(int field15) {
        this.field15 = field15;
    }

    public int getField16() {
        return field16;
    }

    public void setField16(int field16) {
        this.field16 = field16;
    }

    public int getField17() {
        return field17;
    }

    public void setField17(int field17) {
        this.field17 = field17;
    }

    public int getField18() {
        return field18;
    }

    public void setField18(int field18) {
        this.field18 = field18;
    }

    public int getField19() {
        return field19;
    }

    public void setField19(int field19) {
        this.field19 = field19;
    }

    public int getField20() {
        return field20;
    }

    public void setField20(int field20) {
        this.field20 = field20;
    }

    public String getField21() {
        return field21;
    }

    public void setField21(String field21) {
        this.field21 = field21;
    }

    public String getField22() {
        return field22;
    }

    public void setField22(String field22) {
        this.field22 = field22;
    }

    public String getField23() {
        return field23;
    }

    public void setField23(String field23) {
        this.field23 = field23;
    }

    public String getField24() {
        return field24;
    }

    public void setField24(String field24) {
        this.field24 = field24;
    }

    public String getField25() {
        return field25;
    }

    public void setField25(String field25) {
        this.field25 = field25;
    }

    public String getField26() {
        return field26;
    }

    public void setField26(String field26) {
        this.field26 = field26;
    }

    public String getField27() {
        return field27;
    }

    public void setField27(String field27) {
        this.field27 = field27;
    }

    public String getField28() {
        return field28;
    }

    public void setField28(String field28) {
        this.field28 = field28;
    }

    public String getField29() {
        return field29;
    }

    public void setField29(String field29) {
        this.field29 = field29;
    }

    public String getField30() {
        return field30;
    }

    public void setField30(String field30) {
        this.field30 = field30;
    }

    public String getField31() {
        return field31;
    }

    public void setField31(String field31) {
        this.field31 = field31;
    }

    public String getField32() {
        return field32;
    }

    public void setField32(String field32) {
        this.field32 = field32;
    }

    public String getField33() {
        return field33;
    }

    public void setField33(String field33) {
        this.field33 = field33;
    }

    public String getField34() {
        return field34;
    }

    public void setField34(String field34) {
        this.field34 = field34;
    }

    public String getField35() {
        return field35;
    }

    public void setField35(String field35) {
        this.field35 = field35;
    }

    public String getField36() {
        return field36;
    }

    public void setField36(String field36) {
        this.field36 = field36;
    }

    public String getField37() {
        return field37;
    }

    public void setField37(String field37) {
        this.field37 = field37;
    }

    public String getField38() {
        return field38;
    }

    public void setField38(String field38) {
        this.field38 = field38;
    }

    public String getField39() {
        return field39;
    }

    public void setField39(String field39) {
        this.field39 = field39;
    }

    public String getField40() {
        return field40;
    }

    public void setField40(String field40) {
        this.field40 = field40;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LargeEntity that = (LargeEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
