package com.beyondsoft.mina.protocal;

/**
 * @author
 * @date 创建时间：2018年10月12日 上午10:48:40
 * @Description 自定义协议包
 */
public class ProtocolPack {
    private int length;
    private byte flag;
    private String content;

    public ProtocolPack(byte flag, String content) {
        this.flag = flag;
        this.content = content;
        int len1 = content == null ? 0 : content.getBytes().length;
        this.length = 5 + len1;//包头（length+版本信息） + 包体
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("length:").append(length);
        sb.append("flag:").append(flag);
        sb.append("content:").append(content);
        return sb.toString();
    }
}