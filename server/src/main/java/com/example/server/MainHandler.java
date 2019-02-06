package com.example.server;

import com.example.common.FileDelete;
import com.example.common.FileListServer;
import com.example.common.FileMessage;
import com.example.common.FileRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class MainHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg == null) {
                return;
            }
            if (msg instanceof FileRequest) {
                FileRequest fr = (FileRequest) msg;
                if (Files.exists(Paths.get("server_storage/" + fr.getFilename()))) {
                    FileMessage fm = new FileMessage(Paths.get("server_storage/" + fr.getFilename()));
                    ctx.writeAndFlush(fm);
                }
            }
            if (msg instanceof FileMessage) {
                FileMessage fm = (FileMessage) msg;
                if (!Files.exists(Paths.get("server_storage/" + fm.getFilename()))) {
                    Files.write(Paths.get("server_storage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                }
            }
            if (msg instanceof FileDelete) {
                FileDelete fd = (FileDelete) msg;
                if (Files.exists(Paths.get("server_storage/" + fd.getFilename()))) {
                    Files.delete(Paths.get("server_storage/" + fd.getFilename()));
                    ctx.writeAndFlush(fd);
                }
            }
            if (msg instanceof FileListServer) {
                ArrayList<String> arr = new ArrayList<>();
                Files.list(Paths.get("server_storage")).map(p -> p.getFileName().toString()).forEach(o -> arr.add(o));
                FileListServer fileList = new FileListServer(arr);
                ctx.writeAndFlush(fileList);
            }

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
