/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class DeactiavateRequest {

  @ToString.Exclude
  @Size(max = 320)
  @Email
  private String email;

  @Min(0)
  @Max(4)
  private Integer status;

  @ToString.Exclude private String userId;

  public boolean hasAtleastOneRequiredValue() {
    return StringUtils.isNotEmpty(email) || status != null;
  }
}
