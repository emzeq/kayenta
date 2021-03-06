/*
 * Copyright (c) 2019 Nike, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.kayenta.standalonecanaryanalysis.event;

import com.netflix.kayenta.domain.standalonecanaryanalysis.CanaryAnalysisExecutionStatusResponse;
import com.netflix.kayenta.events.AbstractExecutionCompleteEventProcessor;
import com.netflix.kayenta.standalonecanaryanalysis.service.CanaryAnalysisService;
import com.netflix.spinnaker.orca.api.pipeline.models.PipelineExecution;
import com.netflix.spinnaker.orca.pipeline.persistence.ExecutionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class StandaloneCanaryAnalysisExecutionCompletedProducer
    extends AbstractExecutionCompleteEventProcessor {

  private final CanaryAnalysisService canaryAnalysisService;

  public StandaloneCanaryAnalysisExecutionCompletedProducer(
      ApplicationEventPublisher applicationEventPublisher,
      ExecutionRepository executionRepository,
      CanaryAnalysisService canaryAnalysisService) {

    super(applicationEventPublisher, executionRepository);
    this.canaryAnalysisService = canaryAnalysisService;
  }

  @Override
  public boolean shouldProcessExecution(PipelineExecution execution) {
    return CanaryAnalysisService.CANARY_ANALYSIS_PIPELINE_NAME.equals(execution.getName());
  }

  @Override
  public void processCompletedPipelineExecution(PipelineExecution execution) {
    CanaryAnalysisExecutionStatusResponse canaryAnalysisExecution =
        canaryAnalysisService.getCanaryAnalysisExecution(execution.getId(), null);
    applicationEventPublisher.publishEvent(
        new StandaloneCanaryAnalysisExecutionCompletedEvent(this, canaryAnalysisExecution));
  }
}
