package org.jcodec.common;

import org.jcodec.common.model.Packet;

import java.io.IOException;

/**
 * Interface for muxer track that many muxers implement.
 * 
 * @author Stanislav Vitvitskiy
 */
public interface MuxerTrack {

    void addFrame(Packet outPacket) throws IOException;
}
