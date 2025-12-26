package org.jcodec.codecs.aac;

import org.jcodec.codecs.aac.ADTSParser.Header;
import org.jcodec.common.AudioCodecMeta;
import org.jcodec.common.AudioDecoder;
import org.jcodec.common.UsedViaReflection;
import org.jcodec.common.logging.Logger;
import org.jcodec.common.model.AudioBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * This class is part of JCodec ( www.jcodec.org ) This software is distributed
 * under FreeBSD License
 * 
 * Wraps around the JAAD decoder and implements an AudioDecoder interface.
 * 
 * @author Stanislav Vitvitskyy
 */
public class AACDecoder implements AudioDecoder {


    public AACDecoder(ByteBuffer decoderSpecific)  {
        if (decoderSpecific.remaining() >= 7) {
            Header header = ADTSParser.read(decoderSpecific);
            if (header != null) {
                decoderSpecific = ADTSParser.adtsToStreamInfo(header);
            }
            Logger.info("Creating AAC decoder from ADTS header.");
        }
    }

    @Override
    public AudioBuffer decodeFrame(ByteBuffer frame, ByteBuffer dst) throws IOException {
        // Internally all AAC streams are ADTS wrapped
        ADTSParser.read(frame);
        dst.order(ByteOrder.LITTLE_ENDIAN);

        return null;
    }
    

    @Override
    public AudioCodecMeta getCodecMeta(ByteBuffer data) throws IOException {

        return null;
    }

    @UsedViaReflection
    public static int probe(ByteBuffer data) {
        if (data.remaining() < 7)
            return 0;
        Header header = ADTSParser.read(data);
        if (header != null)
            return 100;
        return 0;
    }
}
