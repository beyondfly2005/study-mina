package com.beyondsoft.mina.protocol;

public class ProtocolPack {
    private int length;
    private byte flag;
    private String content;

    public int getLength() {
        return length;
    }

    public byte getFlag() {
        return flag;
    }

    public String getContent() {
        return content;
    }

    public ProtocolPack(byte flag, String content) {
        this.flag = flag;
        this.content = content;
        this.length = (content == null ? 0 : content.length()) + 5;
        //byte 长度为1 ; int length 长度为4
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("length:").append(length);
        sb.append("flag:").append(flag);
        sb.append("content:").append(content);
        return sb.toString();
    }
}
