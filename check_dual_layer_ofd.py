# -*- coding: utf-8 -*-
"""Check OFD dual layer structure"""
import zipfile
import xml.etree.ElementTree as ET

def check_dual_layer_ofd(ofd_path):
    print(f"\nChecking file: {ofd_path}")
    print("=" * 70)
    
    with zipfile.ZipFile(ofd_path, 'r') as z:
        # Check OFD.xml exists
        if 'OFD.xml' not in z.namelist():
            print("ERROR: Missing OFD.xml")
            return False
        
        # Find Content.xml
        content_xml = [f for f in z.namelist() if 'Content.xml' in f]
        if not content_xml:
            print("ERROR: Content.xml not found")
            return False
        
        # Parse first page Content.xml
        with z.open(content_xml[0]) as f:
            tree = ET.parse(f)
            root = tree.getroot()
            ns = {'ofd': 'http://www.ofdspec.org/2016'}
            
            # Find TextObject with Visible="false"
            text_objs = root.findall('.//ofd:TextObject', ns)
            
            print(f"\nFound {len(text_objs)} TextObject(s)")
            
            invisible_count = 0
            for obj in text_objs:
                visible = obj.get('Visible')
                if visible == 'false':
                    invisible_count += 1
                    text_code = obj.find('ofd:TextCode', ns)
                    if text_code is not None:
                        text = text_code.text
                        print(f"  Invisible text: {text[:50]}{'...' if len(text) > 50 else ''}")
            
            if invisible_count == 0:
                print("ERROR: No Visible='false' TextObject found")
                return False
            
            print(f"\nFound {invisible_count} invisible TextObject(s)")
            print("OFD structure is CORRECT for dual-layer document")
            return True

if __name__ == "__main__":
    import sys
    
    # Usage
    if len(sys.argv) > 1:
        ofd_file = sys.argv[1]
    else:
        # Default: check latest file
        ofd_file = r"P:\OCR\Output\output_20260321_201808.ofd"
    
    try:
        result = check_dual_layer_ofd(ofd_file)
        sys.exit(0 if result else 1)
    except Exception as e:
        print(f"\nERROR: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)
