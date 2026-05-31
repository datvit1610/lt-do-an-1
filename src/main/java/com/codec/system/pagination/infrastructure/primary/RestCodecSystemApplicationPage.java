package com.codec.system.pagination.infrastructure.primary;

import codec.error.domain.Assert;
import com.codec.system.pagination.domain.CodecSystemApplicationPage;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

import java.util.List;
import java.util.function.Function;

@Schema(name = "Page", description = "Paginated content")
public class RestCodecSystemApplicationPage<T> {

  private final List<T> content;
  private final int currentPage;
  private final int pageSize;
  private final long currentTotalElementsCount;
  private final int pagesCount;
  private final boolean hasPrevious;
  private final boolean hasNext;
  private final long sumTotalBill;
  private final long sumTotalTransferMoney;

  private RestCodecSystemApplicationPage(RestCodecSystemApplicationPageBuilder<T> builder) {
    content = builder.content;
    currentPage = builder.currentPage;
    pageSize = builder.pageSize;
    currentTotalElementsCount = builder.currentTotalElementsCount;
    pagesCount = builder.pageCount;
    hasPrevious = builder.hasPrevious;
    hasNext = builder.hasNext;
    sumTotalBill = builder.sumTotalBill;
    sumTotalTransferMoney = builder.sumTotalTransferMoney;
  }

  public static <S, T> RestCodecSystemApplicationPage<T> from(CodecSystemApplicationPage<S> source, Function<S, T> mapper) {
    Assert.notNull("source", source);
    Assert.notNull("mapper", mapper);

    return new RestCodecSystemApplicationPageBuilder<>(source.content().parallelStream().map(mapper).toList())
      .currentPage(source.currentPage())
      .pageSize(source.pageSize())
      .currentTotalElementsCount(source.currentTotalElementsCount())
      .pageCount(source.pageCount())
      .hasPrevious(source.hasPrevious())
      .hasNext(source.hasNext())
      .sumTotalBill(source.sumTotalBill())
      .sumTotalTransferMoney(source.sumTotalTransferMoney())
      .build();
  }

  @Schema(description = "Page content")
  public List<T> getContent() {
    return content;
  }

  @Schema(description = "Current page (starts at 0)", example = "0", requiredMode = RequiredMode.REQUIRED)
  public int getCurrentPage() {
    return currentPage;
  }

  @Schema(description = "Number of elements on each page", example = "10", requiredMode = RequiredMode.REQUIRED)
  public int getPageSize() {
    return pageSize;
  }

  @Schema(description = "Total number of elements to paginate", example = "100", requiredMode = RequiredMode.REQUIRED)
  public long getCurrentTotalElementsCount() {
    return currentTotalElementsCount;
  }

  @Schema(description = "Number of resulting pages", example = "10", requiredMode = RequiredMode.REQUIRED)
  public int getPagesCount() {
    return pagesCount;
  }

  @Schema(description = "True is there is a previous page, false otherwise", requiredMode = RequiredMode.REQUIRED)
  public boolean getHasPrevious() {
    return hasPrevious;
  }

  @Schema(description = "True is there is a next page, false otherwise", requiredMode = RequiredMode.REQUIRED)
  public boolean getHasNext() {
    return hasNext;
  }
  @Schema(description = "Total amount of returned bill", requiredMode = RequiredMode.REQUIRED)
  public long getSumTotalBill() {
    return sumTotalBill;
  }
  @Schema(description = "Total amount of returned bill transfer", requiredMode = RequiredMode.REQUIRED)
  public long getSumTotalTransferMoney() {
    return sumTotalTransferMoney;
  }
  private static class RestCodecSystemApplicationPageBuilder<T> {

    private final List<T> content;
    private int currentPage;
    private int pageSize;
    private long currentTotalElementsCount;
    private int pageCount;
    private boolean hasPrevious;
    private boolean hasNext;
    private long sumTotalBill;
    private long sumTotalTransferMoney;

    private RestCodecSystemApplicationPageBuilder(List<T> content) {
      this.content = content;
    }

    public RestCodecSystemApplicationPageBuilder<T> pageSize(int pageSize) {
      this.pageSize = pageSize;

      return this;
    }

    public RestCodecSystemApplicationPageBuilder<T> currentPage(int currentPage) {
      this.currentPage = currentPage;

      return this;
    }

    public RestCodecSystemApplicationPageBuilder<T> currentTotalElementsCount(long currentTotalElementsCount) {
      this.currentTotalElementsCount = currentTotalElementsCount;

      return this;
    }

    public RestCodecSystemApplicationPageBuilder<T> pageCount(int pageCount) {
      this.pageCount = pageCount;

      return this;
    }

    public RestCodecSystemApplicationPageBuilder<T> hasPrevious(boolean hasPrevious) {
      this.hasPrevious = hasPrevious;

      return this;
    }

    public RestCodecSystemApplicationPageBuilder<T> hasNext(boolean hasNext) {
      this.hasNext = hasNext;

      return this;
    }

    public RestCodecSystemApplicationPageBuilder<T> sumTotalBill(long sumTotalBill) {
      this.sumTotalBill = sumTotalBill;

      return this;
    }

    public RestCodecSystemApplicationPageBuilder<T> sumTotalTransferMoney(long sumTotalTransferMoney) {
      this.sumTotalTransferMoney = sumTotalTransferMoney;

      return this;
    }

    public RestCodecSystemApplicationPage<T> build() {
      return new RestCodecSystemApplicationPage<>(this);
    }
  }
}
