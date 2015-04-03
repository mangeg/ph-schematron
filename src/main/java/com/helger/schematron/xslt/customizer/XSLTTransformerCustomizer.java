/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.schematron.xslt.customizer;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.xml.transform.Transformer;

import com.helger.commons.xml.transform.LoggingTransformErrorListener;

/**
 * The default implementation of {@link IXSLTTransformerCustomizer}.
 *
 * @author Philip Helger
 */
public class XSLTTransformerCustomizer extends AbstractTransformerCustomizer <XSLTTransformerCustomizer> implements IXSLTTransformerCustomizer
{
  public XSLTTransformerCustomizer ()
  {}

  @Override
  public void customize (@Nonnull final Transformer aTransformer)
  {
    // Ensure an error listener is present
    if (m_aCustomErrorListener != null)
      aTransformer.setErrorListener (m_aCustomErrorListener);
    else
      aTransformer.setErrorListener (new LoggingTransformErrorListener (Locale.US));

    // Set the optional URI Resolver
    if (m_aCustomURIResolver != null)
      aTransformer.setURIResolver (m_aCustomURIResolver);

    // Set all custom parameters
    if (m_aCustomParameters != null)
      for (final Map.Entry <String, ?> aEntry : m_aCustomParameters.entrySet ())
        aTransformer.setParameter (aEntry.getKey (), aEntry.getValue ());
  }
}