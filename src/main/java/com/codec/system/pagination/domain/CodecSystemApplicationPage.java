package com.codec.system.pagination.domain;

import codec.error.domain.Assert;
import com.codec.system.common.domain.CodecSystemApplicationCollections;

import java.util.List;
import java.util.function.Function;

public class CodecSystemApplicationPage<T> {

  private static final int MINIMAL_PAGE_COUNT = 1;

  private final List<T> content;
  private final int currentPage;
  private final int pageSize;
  private final long currentTotalElementsCount;
  private final long sumTotalBill;
  private final long sumTotalTransferMoney;


  private CodecSystemApplicationPage(CodecSystemApplicationPageBuilder<T> builder) {
    content = CodecSystemApplicationCollections.immutable(builder.content);
    currentPage = builder.currentPage;
    pageSize = buildPageSize(builder.pageSize);
    currentTotalElementsCount = buildCurrentTotalElementsCount(builder.currentTotalElementsCount);
    sumTotalBill = buildSumTotalBill(builder.sumTotalBill);
    sumTotalTransferMoney = buildSumTotalTransferMoney(builder.sumTotalTransferMoney);
  }

  private int buildPageSize(int pageSize) {
    if (pageSize == -1) {
      return content.size();
    }

    return pageSize;
  }

  private long buildCurrentTotalElementsCount(long currentTotalElementsCount) {
    if (currentTotalElementsCount == -1) {
      return content.size();
    }

    return currentTotalElementsCount;
  }
  private long buildSumTotalBill(long sumTotalBill) {
    if (sumTotalBill == -1) {
      return content.size();
    }

    return sumTotalBill;
  }

  private long buildSumTotalTransferMoney(long sumTotalTransferMoney) {
    if (sumTotalTransferMoney == -1) {
      return content.size();
    }

    return sumTotalTransferMoney;
  }
  private long buildTotalElementsCount(long totalElementsCount) {
    if (totalElementsCount == -1) {
      return content.size();
    }

    return totalElementsCount;
  }

  public static <T> CodecSystemApplicationPage<T> singlePage(List<T> content) {
    return builder(content).build();
  }

  public static <T> CodecSystemApplicationPageBuilder<T> builder(List<T> content) {
    return new CodecSystemApplicationPageBuilder<>(content);
  }

  public static <T> CodecSystemApplicationPage<T> of(List<T> elements, CodecSystemApplicationPageable pagination) {
    Assert.notNull("elements", elements);
    Assert.notNull("pagination", pagination);

    List<T> content = elements.subList(
      Math.min(pagination.offset(), elements.size()),
      Math.min(pagination.offset() + pagination.pageSize(), elements.size())
    );

    return builder(content).currentPage(pagination.page()).pageSize(pagination.pageSize()).build();
  }

  public static <T> CodecSystemApplicationPage<T> of(List<T> elements, CodecSystemApplicationPageable pagination, long currentTotalElementsCount) {
    Assert.notNull("elements", elements);
    Assert.notNull("pagination", pagination);

    List<T> content = elements.subList(
      Math.min(pagination.offset(), elements.size()),
      Math.min(pagination.offset() + pagination.pageSize(), elements.size())
    );

    return builder(content).currentPage(pagination.page()).pageSize(pagination.pageSize()).currentTotalElementsCount(currentTotalElementsCount).build();
  }
  public static <T> CodecSystemApplicationPage<T> of(List<T> elements, CodecSystemApplicationPageable pagination, long currentTotalElementsCount, long sumTotalBill, long sumTotalTransferMoney) {
    Assert.notNull("elements", elements);
    Assert.notNull("pagination", pagination);

    List<T> content = elements.subList(
      Math.min(pagination.offset(), elements.size()),
      Math.min(pagination.offset() + pagination.pageSize(), elements.size())
    );

    return builder(content).currentPage(pagination.page()).pageSize(pagination.pageSize()).currentTotalElementsCount(currentTotalElementsCount).sumTotalBill(sumTotalBill).sumTotalTransferMoney(sumTotalTransferMoney).build();
  }
  public List<T> content() {
    return content;
  }

  public int currentPage() {
    return currentPage;
  }

  public int pageSize() {
    return pageSize;
  }

  public long currentTotalElementsCount() {
    return currentTotalElementsCount;
  }
  public long sumTotalBill() {
    return sumTotalBill;
  }
  public long sumTotalTransferMoney() {
    return sumTotalTransferMoney;
  }

  public int pageCount() {
    if (currentTotalElementsCount > 0) {
        return (int) Math.ceil(currentTotalElementsCount / (float) pageSize);
    }

    return MINIMAL_PAGE_COUNT;
  }

  public boolean hasPrevious() {
    return currentPage > 0;
  }

  public boolean hasNext() {
    return isNotLast();
  }

  public boolean isNotLast() {
    return currentPage + 1 < pageCount();
  }

  public <R> CodecSystemApplicationPage<R> map(Function<T, R> mapper) {
    Assert.notNull("mapper", mapper);

    return builder(content().stream().map(mapper).toList())
      .currentPage(currentPage)
      .pageSize(pageSize)
      .build();
  }

  public static class CodecSystemApplicationPageBuilder<T> {

    private final List<T> content;
    private int currentPage;
    private int pageSize = -1;
    private long currentTotalElementsCount = -1;
    private long sumTotalBill;
    private long sumTotalTransferMoney;

    private CodecSystemApplicationPageBuilder(List<T> content) {
      this.content = content;
    }

    public CodecSystemApplicationPageBuilder<T> pageSize(int pageSize) {
      this.pageSize = pageSize;

      return this;
    }

    public CodecSystemApplicationPageBuilder<T> currentPage(int currentPage) {
      this.currentPage = currentPage;

      return this;
    }

    public CodecSystemApplicationPageBuilder<T> currentTotalElementsCount(long currentTotalElementsCount) {
      this.currentTotalElementsCount = currentTotalElementsCount;

      return this;
    }

    public CodecSystemApplicationPageBuilder<T> sumTotalBill(long sumTotalBill) {
      this.sumTotalBill = sumTotalBill;

      return this;
    }
    public CodecSystemApplicationPageBuilder<T> sumTotalTransferMoney(long sumTotalTransferMoney) {
      this.sumTotalTransferMoney = sumTotalTransferMoney;

      return this;
    }

    public CodecSystemApplicationPage<T> build() {
      return new CodecSystemApplicationPage<>(this);
    }
  }
}
