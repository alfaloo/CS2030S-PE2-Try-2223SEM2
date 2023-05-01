/**
 * CS2030S PE2 Question 1
 * AY20/21 Semester 2
 *
 * @author A0000000X
 */

package cs2030s.fp;

public abstract class Try<T> {
  private static final class Success<U> extends Try<U> {
    private final U item;

    private Success(U u) {
      this.item = u;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj instanceof Success) {
        Success<?> success = (Success<?>) obj;
        return this.item == success.item
          ? true
          : this.item == null || success.item == null
          ? false
          : this.item.equals(success.item);
      } else return false;
    }

    @Override
    public U get() throws Throwable {
      return this.item;
    }

    @Override
    public <V> Try<V> map(Transformer<? super U, ? extends V> transformer) {
      try {
        V newItem = transformer.transform(this.item);
        return Try.success(newItem);
      } catch (Throwable throwable) {
        return Try.<V>failure(throwable);
      }
    }

    @Override
    public <V> Try<V> flatMap(Transformer<? super U, ? extends Try<? extends V>> transformer) {
      try {
        Try<? extends V> oneLayer = transformer.transform(this.item);
        return Try.<V>success(oneLayer.get());
      } catch (Throwable throwable) {
        return Try.<V>failure(throwable);
      }
    }

    @Override
    public Try<U> onFailure(Consumer<? super U> consumer) {
      return Try.<U>success(this.item);
    }

    @Override
    public Try<U> recover(Transformer<? super Throwable, ? extends Object> transformer) {
      return Try.<U>success(this.item);
    }
  }

  private static final class Failure extends Try<Throwable> {
    private final Throwable throwable;

    private Failure(Throwable throwable) {
      this.throwable = throwable;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj instanceof Failure) {
        Failure failure = (Failure) obj;
        return this.throwable.toString() == failure.throwable.toString()
          ? true
          : this.throwable == null || failure.throwable == null
          ? false
          : this.throwable.equals(failure.throwable);
      } else return false;
    }

    @Override
    public Throwable get() throws Throwable {
      throw this.throwable;
    }

    @Override
    public <U> Try<U> map(Transformer<? super Throwable, ? extends U> transformer) {
      return Try.<U>failure(this.throwable);
    }

    @Override
    public <U> Try<U> flatMap(Transformer<? super Throwable, ? extends Try<? extends U>> transformer) {
      return Try.<U>failure(this.throwable);
    }

    @Override
    public Try<Throwable> onFailure(Consumer<? super Throwable> consumer) {
      try {
        consumer.consume(this.throwable);
        return Try.<Throwable>failure(this.throwable);
      } catch (Throwable throwable) {
        return Try.<Throwable>failure(throwable);
      }
    }

    @Override
    public Try<? extends Object> recover(Transformer<? super Throwable, ? extends Object> transformer) {
      try {
        return Try.success(transformer.transform(this.throwable));
      } catch (Throwable throwable) {
        return Try.failure(throwable);
      }
    }
  }

  public static <U> Try<U> of(Producer<? extends U> u) {
    try{
      return new Success<>(u.produce());
    } catch (Throwable throwable) {
      @SuppressWarnings("unchecked")
      Try<U> failure = (Try<U>) new Failure(throwable);
      return failure;
    }
  }

  public static <U> Try<U> success(U u) {
    return new Success<>(u);
  }

  public static <U> Try<U> failure(Throwable throwable) {
    @SuppressWarnings("unchecked")
    Try<U> failure = (Try<U>) new Failure(throwable);
    return failure;
  }

  public abstract T get() throws Throwable;

  public abstract <U> Try<U> map(Transformer<? super T, ? extends U> transformer);

  public abstract <U> Try<U> flatMap(Transformer<? super T, ? extends Try<? extends U>> transformer);

  public abstract Try<T> onFailure(Consumer<? super T> consumer);

  public abstract Try<? extends Object> recover(Transformer<? super Throwable, ? extends Object> transformer);
}
