/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.schematron.saxon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;

import com.helger.commons.CGlobal;
import com.helger.commons.exception.InitializationException;
import com.helger.commons.lang.ClassLoaderHelper;
import com.helger.xml.transform.DefaultTransformURIResolver;
import com.helger.xml.transform.LoggingTransformErrorListener;

/**
 * A special {@link TransformerFactory} handler that prefers Saxon's
 * {@link TransformerFactory} before calling the SPI version
 * <code>TransformerFactory.newInstance ()</code>. This is mainly to solve the
 * interoperability issue when using Xalan and Saxon together in the class path.
 *
 * @author Philip Helger
 */
@Immutable
public final class SchematronTransformerFactory
{
  public static final String SAXON_TRANSFORMER_FACTORY_CLASS = "net.sf.saxon.TransformerFactoryImpl";
  private static final TransformerFactory s_aDefaultFactory;

  static
  {
    s_aDefaultFactory = createTransformerFactorySaxonFirst ((ClassLoader) null,
                                                            new LoggingTransformErrorListener (CGlobal.DEFAULT_LOCALE),
                                                            new DefaultTransformURIResolver ());
  }

  private SchematronTransformerFactory ()
  {}

  /**
   * @return The default "Saxon first" {@link TransformerFactory}. Never
   *         <code>null</code>.
   */
  @Nonnull
  public static TransformerFactory getDefaultSaxonFirst ()
  {
    return s_aDefaultFactory;
  }

  /**
   * Create a new {@link TransformerFactory} trying to invoke the Saxon
   * implementation first using the class
   * {@value #SAXON_TRANSFORMER_FACTORY_CLASS}.
   *
   * @param aClassLoader
   *        The optional class loader to be used. May be <code>null</code>.
   * @param aErrorListener
   *        An optional XSLT error listener to be used. May be
   *        <code>null</code>.
   * @param aURIResolver
   *        An optional XSLT URI resolver to be used. May be <code>null</code>.
   * @return A new {@link TransformerFactory} and not <code>null</code>.
   * @throws InitializationException
   *         In case initialization fails.
   */
  @Nonnull
  public static TransformerFactory createTransformerFactorySaxonFirst (@Nullable final ClassLoader aClassLoader,
                                                                       @Nullable final ErrorListener aErrorListener,
                                                                       @Nullable final URIResolver aURIResolver)
  {
    TransformerFactory aFactory;
    try
    {
      // Try Saxon first
      final ClassLoader aEffectiveClassLoader = aClassLoader != null ? aClassLoader
                                                                     : ClassLoaderHelper.getContextClassLoader ();
      aFactory = TransformerFactory.newInstance (SAXON_TRANSFORMER_FACTORY_CLASS, aEffectiveClassLoader);
    }
    catch (final TransformerFactoryConfigurationError ex)
    {
      try
      {
        // Try default afterwards
        aFactory = TransformerFactory.newInstance ();
      }
      catch (final TransformerFactoryConfigurationError ex2)
      {
        throw new InitializationException ("Failed to create XML TransformerFactory", ex2);
      }
    }

    if (aErrorListener != null)
      aFactory.setErrorListener (aErrorListener);
    if (aURIResolver != null)
      aFactory.setURIResolver (aURIResolver);
    return aFactory;
  }
}
