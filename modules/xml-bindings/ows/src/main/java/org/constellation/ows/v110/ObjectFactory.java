/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.constellation.ows.v110;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.ows._1 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Role_QNAME = new QName("http://www.opengis.net/ows/1.1", "Role");
    private final static QName _Range_QNAME = new QName("http://www.opengis.net/ows/1.1", "Range");
    private final static QName _Keywords_QNAME = new QName("http://www.opengis.net/ows/1.1", "Keywords");
    private final static QName _Manifest_QNAME = new QName("http://www.opengis.net/ows/1.1", "Manifest");
    private final static QName _AbstractReferenceBase_QNAME = new QName("http://www.opengis.net/ows/1.1", "AbstractReferenceBase");
    private final static QName _OtherSource_QNAME = new QName("http://www.opengis.net/ows/1.1", "OtherSource");
    private final static QName _Fees_QNAME = new QName("http://www.opengis.net/ows/1.1", "Fees");
    private final static QName _InputData_QNAME = new QName("http://www.opengis.net/ows/1.1", "InputData");
    private final static QName _Reference_QNAME = new QName("http://www.opengis.net/ows/1.1", "Reference");
    private final static QName _SupportedCRS_QNAME = new QName("http://www.opengis.net/ows/1.1", "SupportedCRS");
    private final static QName _OutputFormat_QNAME = new QName("http://www.opengis.net/ows/1.1", "OutputFormat");
    private final static QName _OperationResponse_QNAME = new QName("http://www.opengis.net/ows/1.1", "OperationResponse");
    private final static QName _ExtendedCapabilities_QNAME = new QName("http://www.opengis.net/ows/1.1", "ExtendedCapabilities");
    private final static QName _Identifier_QNAME = new QName("http://www.opengis.net/ows/1.1", "Identifier");
    private final static QName _OrganisationName_QNAME = new QName("http://www.opengis.net/ows/1.1", "OrganisationName");
    private final static QName _DataType_QNAME = new QName("http://www.opengis.net/ows/1.1", "DataType");
    private final static QName _WGS84BoundingBox_QNAME = new QName("http://www.opengis.net/ows/1.1", "WGS84BoundingBox");
    private final static QName _Spacing_QNAME = new QName("http://www.opengis.net/ows/1.1", "Spacing");
    private final static QName _ReferenceGroup_QNAME = new QName("http://www.opengis.net/ows/1.1", "ReferenceGroup");
    private final static QName _MinimumValue_QNAME = new QName("http://www.opengis.net/ows/1.1", "MinimumValue");
    private final static QName _BoundingBox_QNAME = new QName("http://www.opengis.net/ows/1.1", "BoundingBox");
    private final static QName _PositionName_QNAME = new QName("http://www.opengis.net/ows/1.1", "PositionName");
    private final static QName _Meaning_QNAME = new QName("http://www.opengis.net/ows/1.1", "Meaning");
    private final static QName _DefaultValue_QNAME = new QName("http://www.opengis.net/ows/1.1", "DefaultValue");
    private final static QName _Language_QNAME = new QName("http://www.opengis.net/ows/1.1", "Language");
    private final static QName _IndividualName_QNAME = new QName("http://www.opengis.net/ows/1.1", "IndividualName");
    private final static QName _Title_QNAME = new QName("http://www.opengis.net/ows/1.1", "Title");
    private final static QName _ReferenceSystem_QNAME = new QName("http://www.opengis.net/ows/1.1", "ReferenceSystem");
    private final static QName _AvailableCRS_QNAME = new QName("http://www.opengis.net/ows/1.1", "AvailableCRS");
    private final static QName _UOM_QNAME = new QName("http://www.opengis.net/ows/1.1", "UOM");
    private final static QName _Resource_QNAME = new QName("http://www.opengis.net/ows/1.1", "Resource");
    private final static QName _ContactInfo_QNAME = new QName("http://www.opengis.net/ows/1.1", "ContactInfo");
    private final static QName _MaximumValue_QNAME = new QName("http://www.opengis.net/ows/1.1", "MaximumValue");
    private final static QName _Value_QNAME = new QName("http://www.opengis.net/ows/1.1", "Value");
    private final static QName _DatasetDescriptionSummary_QNAME = new QName("http://www.opengis.net/ows/1.1", "DatasetDescriptionSummary");
    private final static QName _Abstract_QNAME = new QName("http://www.opengis.net/ows/1.1", "Abstract");
    private final static QName _ServiceReference_QNAME = new QName("http://www.opengis.net/ows/1.1", "ServiceReference");
    private final static QName _Metadata_QNAME = new QName("http://www.opengis.net/ows/1.1", "Metadata");
    private final static QName _AbstractMetaData_QNAME = new QName("http://www.opengis.net/ows/1.1", "AbstractMetaData");
    private final static QName _AccessConstraints_QNAME = new QName("http://www.opengis.net/ows/1.1", "AccessConstraints");
    private final static QName _HTTPGet_QNAME = new QName("http://www.opengis.net/ows/1.1", "Get");
    private final static QName _HTTPPost_QNAME = new QName("http://www.opengis.net/ows/1.1", "Post");
    private final static QName _Exception_QNAME = new QName("http://www.opengis.net/ows/1.1", "Exception");
    private final static QName _GetCapabilities_QNAME = new QName("http://www.opengis.net/ows/1.1", "GetCapabilities");
    
    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.ows._1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RangeType }
     * 
     */
    public RangeType createRangeType() {
        return new RangeType();
    }

    /**
     * Create an instance of {@link ValuesReference }
     * 
     */
    public ValuesReference createValuesReference() {
        return new ValuesReference();
    }

    /**
     * Create an instance of {@link CapabilitiesBaseType }
     * 
     */
    public CapabilitiesBaseType createCapabilitiesBaseType() {
        return new CapabilitiesBaseType();
    }

    /**
     * Create an instance of {@link ReferenceGroupType }
     * 
     */
    public ReferenceGroupType createReferenceGroupType() {
        return new ReferenceGroupType();
    }

    /**
     * Create an instance of {@link OperationsMetadata }
     * 
     */
    public OperationsMetadata createOperationsMetadata() {
        return new OperationsMetadata();
    }

    /**
     * Create an instance of {@link HTTP }
     * 
     */
    public HTTP createHTTP() {
        return new HTTP();
    }

    /**
     * Create an instance of {@link BoundingBoxType }
     * 
     */
    public BoundingBoxType createBoundingBoxType() {
        return new BoundingBoxType();
    }

    /**
     * Create an instance of {@link CodeType }
     * 
     */
    public CodeType createCodeType() {
        return new CodeType();
    }

    /**
     * Create an instance of {@link AddressType }
     * 
     */
    public AddressType createAddressType() {
        return new AddressType();
    }

    /**
     * Create an instance of {@link DCP }
     * 
     */
    public DCP createDCP() {
        return new DCP();
    }

    /**
     * Create an instance of {@link DatasetDescriptionSummaryBaseType }
     *
     */
    public DatasetDescriptionSummaryBaseType createDatasetDescriptionSummaryBaseType() {
        return new DatasetDescriptionSummaryBaseType();
    }

    /**
     * Create an instance of {@link DescriptionType }
     * 
     */
    public DescriptionType createDescriptionType() {
        return new DescriptionType();
    }

    /**
     * Create an instance of {@link IdentificationType }
     * 
     */
    public IdentificationType createIdentificationType() {
        return new IdentificationType();
    }

    /**
     * Create an instance of {@link DomainMetadataType }
     * 
     */
    public DomainMetadataType createDomainMetadataType() {
        return new DomainMetadataType();
    }

    /**
     * Create an instance of {@link WGS84BoundingBoxType }
     * 
     */
    public WGS84BoundingBoxType createWGS84BoundingBoxType() {
        return new WGS84BoundingBoxType();
    }

    /**
     * Create an instance of {@link AbstractReferenceBaseType }
     * 
     */
    public AbstractReferenceBaseType createAbstractReferenceBaseType() {
        return new AbstractReferenceBaseType();
    }

    /**
     * Create an instance of {@link KeywordsType }
     * 
     */
    public KeywordsType createKeywordsType() {
        return new KeywordsType();
    }

    /**
     * Create an instance of {@link OnlineResourceType }
     * 
     */
    public OnlineResourceType createOnlineResourceType() {
        return new OnlineResourceType();
    }

    /**
     * Create an instance of {@link ManifestType }
     * 
     */
    public ManifestType createManifestType() {
        return new ManifestType();
    }

    /**
     * Create an instance of {@link LanguageStringType }
     * 
     */
    public LanguageStringType createLanguageStringType() {
        return new LanguageStringType();
    }

    /**
     * Create an instance of {@link AllowedValues }
     * 
     */
    public AllowedValues createAllowedValues() {
        return new AllowedValues();
    }

    /**
     * Create an instance of {@link Operation }
     * 
     */
    public Operation createOperation() {
        return new Operation();
    }

    /**
     * Create an instance of {@link UnNamedDomainType }
     * 
     */
    public UnNamedDomainType createUnNamedDomainType() {
        return new UnNamedDomainType();
    }

   /**
     * Create an instance of {@link ServiceIdentification }
     * 
     */
    public ServiceIdentification createServiceIdentification() {
        return new ServiceIdentification();
    }

    /**
     * Create an instance of {@link ServiceProvider }
     * 
     */
    public ServiceProvider createServiceProvider() {
        return new ServiceProvider();
    }

    /**
     * Create an instance of {@link AnyValue }
     * 
     */
    public AnyValue createAnyValue() {
        return new AnyValue();
    }

   /**
     * Create an instance of {@link ReferenceType }
     * 
     */
    public ReferenceType createReferenceType() {
        return new ReferenceType();
    }

    /**
     * Create an instance of {@link NoValues }
     * 
     */
    public NoValues createNoValues() {
        return new NoValues();
    }

    /**
     * Create an instance of {@link MetadataType }
     * 
     */
    public MetadataType createMetadataType() {
        return new MetadataType();
    }

    /**
     * Create an instance of {@link TelephoneType }
     * 
     */
    public TelephoneType createTelephoneType() {
        return new TelephoneType();
    }

    /**
     * Create an instance of {@link RequestMethodType }
     * 
     */
    public RequestMethodType createRequestMethodType() {
        return new RequestMethodType();
    }

    /**
     * Create an instance of {@link ResponsiblePartySubsetType }
     * 
     */
    public ResponsiblePartySubsetType createResponsiblePartySubsetType() {
        return new ResponsiblePartySubsetType();
    }

    /**
     * Create an instance of {@link BasicIdentificationType }
     * 
     */
    public BasicIdentificationType createBasicIdentificationType() {
        return new BasicIdentificationType();
    }

    /**
     * Create an instance of {@link DomainType }
     * 
     */
    public DomainType createDomainType() {
        return new DomainType();
    }

    /**
     * Create an instance of {@link ContactType }
     * 
     */
    public ContactType createContactType() {
        return new ContactType();
    }

    /**
     * Create an instance of {@link ContentsBaseType }
     *
     */
    public ContentsBaseType createContentsBaseType() {
        return new ContentsBaseType();
    }

    /**
     * Create an instance of {@link ServiceReferenceType }
     * 
     */
    public ServiceReferenceType createServiceReferenceType() {
        return new ServiceReferenceType();
    }

    /**
     * Create an instance of {@link ValueType }
     * 
     */
    public ValueType createValueType() {
        return new ValueType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "Role")
    public JAXBElement<CodeType> createRole(CodeType value) {
        return new JAXBElement<CodeType>(_Role_QNAME, CodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RangeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "Range")
    public JAXBElement<RangeType> createRange(RangeType value) {
        return new JAXBElement<RangeType>(_Range_QNAME, RangeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KeywordsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "Keywords")
    public JAXBElement<KeywordsType> createKeywords(KeywordsType value) {
        return new JAXBElement<KeywordsType>(_Keywords_QNAME, KeywordsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ManifestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "Manifest")
    public JAXBElement<ManifestType> createManifest(ManifestType value) {
        return new JAXBElement<ManifestType>(_Manifest_QNAME, ManifestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractReferenceBaseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "AbstractReferenceBase")
    public JAXBElement<AbstractReferenceBaseType> createAbstractReferenceBase(AbstractReferenceBaseType value) {
        return new JAXBElement<AbstractReferenceBaseType>(_AbstractReferenceBase_QNAME, AbstractReferenceBaseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MetadataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "OtherSource")
    public JAXBElement<MetadataType> createOtherSource(MetadataType value) {
        return new JAXBElement<MetadataType>(_OtherSource_QNAME, MetadataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "Fees")
    public JAXBElement<String> createFees(String value) {
        return new JAXBElement<String>(_Fees_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ManifestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "InputData")
    public JAXBElement<ManifestType> createInputData(ManifestType value) {
        return new JAXBElement<ManifestType>(_InputData_QNAME, ManifestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "Reference", substitutionHeadNamespace = "http://www.opengis.net/ows/1.1", substitutionHeadName = "AbstractReferenceBase")
    public JAXBElement<ReferenceType> createReference(ReferenceType value) {
        return new JAXBElement<ReferenceType>(_Reference_QNAME, ReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "SupportedCRS", substitutionHeadNamespace = "http://www.opengis.net/ows/1.1", substitutionHeadName = "AvailableCRS")
    public JAXBElement<String> createSupportedCRS(String value) {
        return new JAXBElement<String>(_SupportedCRS_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "OutputFormat")
    public JAXBElement<String> createOutputFormat(String value) {
        return new JAXBElement<String>(_OutputFormat_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ManifestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "OperationResponse")
    public JAXBElement<ManifestType> createOperationResponse(ManifestType value) {
        return new JAXBElement<ManifestType>(_OperationResponse_QNAME, ManifestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "ExtendedCapabilities")
    public JAXBElement<Object> createExtendedCapabilities(Object value) {
        return new JAXBElement<Object>(_ExtendedCapabilities_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "Identifier")
    public JAXBElement<CodeType> createIdentifier(CodeType value) {
        return new JAXBElement<CodeType>(_Identifier_QNAME, CodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "OrganisationName")
    public JAXBElement<String> createOrganisationName(String value) {
        return new JAXBElement<String>(_OrganisationName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DomainMetadataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "DataType")
    public JAXBElement<DomainMetadataType> createDataType(DomainMetadataType value) {
        return new JAXBElement<DomainMetadataType>(_DataType_QNAME, DomainMetadataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WGS84BoundingBoxType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "WGS84BoundingBox", substitutionHeadNamespace = "http://www.opengis.net/ows/1.1", substitutionHeadName = "BoundingBox")
    public JAXBElement<WGS84BoundingBoxType> createWGS84BoundingBox(WGS84BoundingBoxType value) {
        return new JAXBElement<WGS84BoundingBoxType>(_WGS84BoundingBox_QNAME, WGS84BoundingBoxType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValueType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "Spacing")
    public JAXBElement<ValueType> createSpacing(ValueType value) {
        return new JAXBElement<ValueType>(_Spacing_QNAME, ValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReferenceGroupType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "ReferenceGroup")
    public JAXBElement<ReferenceGroupType> createReferenceGroup(ReferenceGroupType value) {
        return new JAXBElement<ReferenceGroupType>(_ReferenceGroup_QNAME, ReferenceGroupType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValueType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "MinimumValue")
    public JAXBElement<ValueType> createMinimumValue(ValueType value) {
        return new JAXBElement<ValueType>(_MinimumValue_QNAME, ValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BoundingBoxType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "BoundingBox")
    public JAXBElement<BoundingBoxType> createBoundingBox(BoundingBoxType value) {
        return new JAXBElement<BoundingBoxType>(_BoundingBox_QNAME, BoundingBoxType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "PositionName")
    public JAXBElement<String> createPositionName(String value) {
        return new JAXBElement<String>(_PositionName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DomainMetadataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "Meaning")
    public JAXBElement<DomainMetadataType> createMeaning(DomainMetadataType value) {
        return new JAXBElement<DomainMetadataType>(_Meaning_QNAME, DomainMetadataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValueType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "DefaultValue")
    public JAXBElement<ValueType> createDefaultValue(ValueType value) {
        return new JAXBElement<ValueType>(_DefaultValue_QNAME, ValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "Language")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createLanguage(String value) {
        return new JAXBElement<String>(_Language_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "IndividualName")
    public JAXBElement<String> createIndividualName(String value) {
        return new JAXBElement<String>(_IndividualName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LanguageStringType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "Title")
    public JAXBElement<LanguageStringType> createTitle(LanguageStringType value) {
        return new JAXBElement<LanguageStringType>(_Title_QNAME, LanguageStringType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DomainMetadataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "ReferenceSystem")
    public JAXBElement<DomainMetadataType> createReferenceSystem(DomainMetadataType value) {
        return new JAXBElement<DomainMetadataType>(_ReferenceSystem_QNAME, DomainMetadataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "AvailableCRS")
    public JAXBElement<String> createAvailableCRS(String value) {
        return new JAXBElement<String>(_AvailableCRS_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DomainMetadataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "UOM")
    public JAXBElement<DomainMetadataType> createUOM(DomainMetadataType value) {
        return new JAXBElement<DomainMetadataType>(_UOM_QNAME, DomainMetadataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "Resource")
    public JAXBElement<Object> createResource(Object value) {
        return new JAXBElement<Object>(_Resource_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContactType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "ContactInfo")
    public JAXBElement<ContactType> createContactInfo(ContactType value) {
        return new JAXBElement<ContactType>(_ContactInfo_QNAME, ContactType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValueType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "MaximumValue")
    public JAXBElement<ValueType> createMaximumValue(ValueType value) {
        return new JAXBElement<ValueType>(_MaximumValue_QNAME, ValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValueType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "Value")
    public JAXBElement<ValueType> createValue(ValueType value) {
        return new JAXBElement<ValueType>(_Value_QNAME, ValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LanguageStringType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "Abstract")
    public JAXBElement<LanguageStringType> createAbstract(LanguageStringType value) {
        return new JAXBElement<LanguageStringType>(_Abstract_QNAME, LanguageStringType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceReferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "ServiceReference", substitutionHeadNamespace = "http://www.opengis.net/ows/1.1", substitutionHeadName = "Reference")
    public JAXBElement<ServiceReferenceType> createServiceReference(ServiceReferenceType value) {
        return new JAXBElement<ServiceReferenceType>(_ServiceReference_QNAME, ServiceReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MetadataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "Metadata")
    public JAXBElement<MetadataType> createMetadata(MetadataType value) {
        return new JAXBElement<MetadataType>(_Metadata_QNAME, MetadataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DatasetDescriptionSummaryBaseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "DatasetDescriptionSummary")
    public JAXBElement<DatasetDescriptionSummaryBaseType> createDatasetDescriptionSummary(DatasetDescriptionSummaryBaseType value) {
        return new JAXBElement<DatasetDescriptionSummaryBaseType>(_DatasetDescriptionSummary_QNAME, DatasetDescriptionSummaryBaseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "AbstractMetaData")
    public JAXBElement<Object> createAbstractMetaData(Object value) {
        return new JAXBElement<Object>(_AbstractMetaData_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "AccessConstraints")
    public JAXBElement<String> createAccessConstraints(String value) {
        return new JAXBElement<String>(_AccessConstraints_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestMethodType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "Get", scope = HTTP.class)
    public JAXBElement<RequestMethodType> createHTTPGet(RequestMethodType value) {
        return new JAXBElement<RequestMethodType>(_HTTPGet_QNAME, RequestMethodType.class, HTTP.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestMethodType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "Post", scope = HTTP.class)
    public JAXBElement<RequestMethodType> createHTTPPost(RequestMethodType value) {
        return new JAXBElement<RequestMethodType>(_HTTPPost_QNAME, RequestMethodType.class, HTTP.class, value);
    }
    
     /**
     * Create an instance of {@link ExceptionReport }
     * 
     */
    public ExceptionReport createExceptionReport() {
        return new ExceptionReport();
    }

    /**
     * Create an instance of {@link ExceptionType }
     * 
     */
    public ExceptionType createExceptionType() {
        return new ExceptionType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExceptionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "Exception")
    public JAXBElement<ExceptionType> createException(ExceptionType value) {
        return new JAXBElement<ExceptionType>(_Exception_QNAME, ExceptionType.class, null, value);
    }

    /**
     * Create an instance of {@link SectionsType }
     * 
     */
    public SectionsType createSectionsType() {
        return new SectionsType();
    }

   /**
     * Create an instance of {@link GetCapabilitiesType }
     * 
     */
    public GetCapabilitiesType createGetCapabilitiesType() {
        return new GetCapabilitiesType();
    }

    /**
     * Create an instance of {@link AcceptFormatsType }
     * 
     */
    public AcceptFormatsType createAcceptFormatsType() {
        return new AcceptFormatsType();
    }

    /**
     * Create an instance of {@link AcceptVersionsType }
     * 
     */
    public AcceptVersionsType createAcceptVersionsType() {
        return new AcceptVersionsType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCapabilitiesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows/1.1", name = "GetCapabilities")
    public JAXBElement<GetCapabilitiesType> createGetCapabilities(GetCapabilitiesType value) {
        return new JAXBElement<GetCapabilitiesType>(_GetCapabilities_QNAME, GetCapabilitiesType.class, null, value);
    }

}
