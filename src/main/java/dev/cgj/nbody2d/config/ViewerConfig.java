package dev.cgj.nbody2d.config;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ViewerConfig {

    long repaintInterval;

    long autoStepInterval;
}
