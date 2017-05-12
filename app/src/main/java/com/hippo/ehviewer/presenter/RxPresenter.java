/*
 * Copyright 2017 Hippo Seven
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

package com.hippo.ehviewer.presenter;

/*
 * Created by Hippo on 5/12/2017.
 */

import com.hippo.ehviewer.view.ViewInterface;
import java.util.concurrent.TimeUnit;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.subscriptions.CompositeSubscription;

/**
 * {@code RxPresenter} has some ReactiveX features.
 * <p>
 * It holds a {@link CompositeSubscription} which is
 * unsubscribed in {@link #onDestroy()}. The {@link CompositeSubscription}
 * can be get from {@link #getSubscriptionSet()}.
 * <p>
 * It supports scheduling actions to UI thread.
 */
public abstract class RxPresenter<V extends ViewInterface> extends ScenePresenter<V> {

  private Scheduler.Worker worker = AndroidSchedulers.mainThread().createWorker();
  private CompositeSubscription subscriptionSet = new CompositeSubscription();

  @Override
  protected void onDestroy() {
    super.onDestroy();
    worker.unsubscribe();
    subscriptionSet.unsubscribe();
  }

  /**
   * Schedules an Action for execution in UI thread.
   * <p>
   * The action will be cancelled after the presenter destroyed.
   * <p>
   * Returns {@code Subscriptions.unsubscribed()} if the presenter is already destroyed.
   */
  public Subscription schedule(Action0 action) {
    return worker.schedule(action);
  }

  /**
   * Schedules an action for execution at some point in the future
   * and in UI thread.
   * <p>
   * The action will be cancelled after the presenter detached.
   * <p>
   * Returns {@code Subscriptions.unsubscribed()} if the presenter is already detached.
   */
  public Subscription schedule(Action0 action, long delayMillis) {
    return worker.schedule(action, delayMillis, TimeUnit.MILLISECONDS);
  }

  /**
   * Schedules an action to be executed periodically in UI thread.
   * <p>
   * The action will be cancelled after the presenter detached.
   * <p>
   * Returns {@code Subscriptions.unsubscribed()} if the presenter is already detached.
   */
  public Subscription schedulePeriodically(final Action0 action, long delayMillis,
      long periodMillis) {
    return worker.schedulePeriodically(action, delayMillis, periodMillis, TimeUnit.MILLISECONDS);
  }

  /**
   * Returns the {@link CompositeSubscription}.
   */
  protected CompositeSubscription getSubscriptionSet() {
    return subscriptionSet;
  }
}
