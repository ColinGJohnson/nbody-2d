package dev.cgj.nbody2d.config;

import lombok.Builder;
import lombok.Data;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@With
@Jacksonized
public class ViewerConfig {

    long repaintInterval;

    long autoStepInterval;

    /**
     * Should trails be drawn?
     */
    boolean showTrails;

    /**
     * Should trails be colored?
     */
    boolean colorTrails;

    /**
     * Should force vectors be rendered?
     */
    boolean showForceVectors;
}
