package com.twosigma.testing.reporter;

import com.twosigma.utils.TraceUtils;

import java.util.*;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

/**
 * @author mykola
 */
public class TestStep<E> {
    private E context;

    private TokenizedMessage inProgressMessage;
    private Supplier<TokenizedMessage> completionMessageSupplier;
    private Runnable action;
    private TokenizedMessage completionMessage;

    private boolean isInProgress;
    private boolean isSuccessful;

    private List<TestStep<?>> children;
    private TestStep<?> parent;
    private String stackTrace;

    private List<TestStepPayload> payloads;

    private static ThreadLocal<TestStep<?>> currentStep = new ThreadLocal<>();

    public static <E> TestStep<E> create(E context,
                                         TokenizedMessage inProgressMessage,
                                         Supplier<TokenizedMessage> completionMessageSupplier,
                                         Runnable action) {
        TestStep<E> step = new TestStep<>(context, inProgressMessage, completionMessageSupplier, action);
        TestStep<?> localCurrentStep = TestStep.currentStep.get();

        step.parent = localCurrentStep;
        if (localCurrentStep != null) {
            localCurrentStep.children.add(step);
        }
        currentStep.set(step);

        return step;
    }

    private TestStep(E context,
                     TokenizedMessage inProgressMessage,
                     Supplier<TokenizedMessage> completionMessageSupplier,
                     Runnable action) {
        this.context = context;
        this.children = new ArrayList<>();
        this.inProgressMessage = inProgressMessage;
        this.completionMessageSupplier = completionMessageSupplier;
        this.action = action;
        this.isInProgress = true;
        this.payloads = new ArrayList<>();
    }

    public List<TestStepPayload> getCombinedPayloads() {
        List<TestStepPayload> result = new ArrayList<>();
        result.addAll(payloads);
        children.forEach(s -> result.addAll(s.getCombinedPayloads()));

        return result;
    }

    public void addPayload(TestStepPayload payload) {
        payloads.add(payload);
    }

    public boolean hasPayload(Class<?> type) {
        return payloads.stream().anyMatch(p -> type.isAssignableFrom(type)) ||
                children.stream().anyMatch(s -> s.hasPayload(type));
    }

    @SuppressWarnings("unchecked")
    public E getFirstAvailableContext() {
        if (context != null) {
            return context;
        }

        return (E) children.stream()
                .map(TestStep::getFirstAvailableContext)
                .filter(Objects::nonNull)
                .findFirst().orElse(null);
    }

    public int getNumberOfParents() {
        int result = 0;
        TestStep step = this;
        while (step.parent != null) {
            result++;
            step = step.parent;
        }

        return result;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public boolean isFailed() {
        return ! isSuccessful;
    }

    public void execute() {
        try {
            StepReporters.onStart(this);
            action.run();

            complete(completionMessageSupplier.get());
            StepReporters.onSuccess(this);
        } catch (Throwable e) {
            fail(e);
            StepReporters.onFailure(this);
            throw e;
        } finally {
            TestStep<?> localCurrentStep = TestStep.currentStep.get();
            if (localCurrentStep != null) {
                currentStep.set(localCurrentStep.parent);
            }
        }
    }

    public TokenizedMessage getInProgressMessage() {
        return inProgressMessage;
    }

    public TokenizedMessage getCompletionMessage() {
        return completionMessage;
    }

    public Map<String, ?> toMap() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", completionMessage.toListOfMaps());

        if (! children.isEmpty()) {
            result.put("children", children.stream().map(TestStep::toMap).collect(toList()));
        }

        return result;
    }

    private void complete(TokenizedMessage message) {
        isInProgress = false;
        isSuccessful = true;
        completionMessage = message;
    }

    private void fail(Throwable t) {
        stackTrace = TraceUtils.stackTrace(t);
        completionMessage = new TokenizedMessage();
        completionMessage.add("error", "failed").add(inProgressMessage).add("delimiter", ":")
                .add("error", t.getMessage());
    }
}
