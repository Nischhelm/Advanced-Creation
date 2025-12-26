package org.jcodec.api.transcode;

import org.jcodec.common.model.Packet;

import java.io.IOException;


/**
 * A source for compressed video/audio frames.
 * 
 * @author Stanislav Vitvitskiy
 */
public interface PacketSource {

    Packet inputVideoPacket() throws IOException;

    Packet inputAudioPacket() throws IOException;
    
}
