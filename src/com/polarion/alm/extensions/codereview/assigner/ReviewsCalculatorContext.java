/*
 * Copyright 2016 Polarion AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.polarion.alm.extensions.codereview.assigner;

import java.util.Iterator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.polarion.alm.tracker.model.IWorkItem;
import com.polarion.platform.persistence.model.IRevision;

interface ReviewsCalculatorContext {

    @NotNull
    IRevision getRevisionOfHistoricalWorkItem(@NotNull IWorkItem historicalWorkItem);

    @NotNull
    Iterator<IWorkItem> getHistoryOfWorkItemFromNewest(@NotNull IWorkItem workItem);

    boolean wasReviewWorkflowActionTriggered(@NotNull IWorkItem workItem);

    boolean isReviewer(@NotNull IWorkItem contextWorkItem, @Nullable String user);

    void log(@NotNull String message);

}