解码本地没问题；
解码网络端口有问题：
Avformat_open_input("test.sdp");
只有读取有BOM的Unicode-littleendian的sdp文件返回值才正常，但是下一步
找Stream的时候返回AVERROR_EOF;